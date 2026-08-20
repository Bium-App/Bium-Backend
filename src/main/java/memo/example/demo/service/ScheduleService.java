package memo.example.demo.service;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.ScheduleRequestDto;
import memo.example.demo.DTO.response.ScheduleResponseDto;
import memo.example.demo.domain.Schedule;
import memo.example.demo.domain.TeamSpace;
import memo.example.demo.domain.User;
import memo.example.demo.repository.ScheduleRepository; import memo.example.demo.repository.TeamSpaceRepository; import memo.example.demo.repository.UserRepository; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 개인 및 팀 일정을 저장하고 시작 시각의 연도와 월을 기준으로 조회한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final TeamSpaceRepository teamSpaceRepository;

    public void createSchedule(Long userId, ScheduleRequestDto request) {
        // 요청의 teamSpaceId로 연결할 TeamSpace를 조회해 일정의 저장 대상을 결정한다.
        User user = userRepository.findById(userId).orElseThrow();
        TeamSpace teamSpace = request.getTeamSpaceId() != null ?
                teamSpaceRepository.findById(request.getTeamSpaceId()).orElse(null) : null;
        Schedule schedule = Schedule.builder()
                .user(user)
                .teamSpace(teamSpace)
                .sTitle(request.getTitle())
                .sContent(request.getContent())
                .startAt(parseDateTimeSafe(request.getStartAt()))
                .endAt(parseDateTimeSafe(request.getEndAt()))
                .build();
        scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getTeamSchedulesByMonth(Long teamSpaceId, int year, int month) {
        return scheduleRepository.findByTeamSpaceAndMonth(teamSpaceId, year, month).stream()
                .map(ScheduleResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getUserSchedulesByMonth(Long userId, int year, int month) {
        return scheduleRepository.findByUserAndMonth(userId, year, month).stream()
                .map(ScheduleResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDto getScheduleDetail(Long scheduleId) {
        Schedule s = scheduleRepository.findById(scheduleId).orElseThrow();
        return ScheduleResponseDto.from(s);
    }

    public void updateSchedule(Long scheduleId, ScheduleRequestDto request) {
        Schedule s = scheduleRepository.findById(scheduleId).orElseThrow();
        // 요청에 포함된 값만 변경해 전달되지 않은 일정 정보는 유지한다.
        if (request.getTitle() != null) s.setSTitle(request.getTitle());
        if (request.getContent() != null) s.setSContent(request.getContent());
        if (request.getStartAt() != null) s.setStartAt(parseDateTimeSafe(request.getStartAt()));
        if (request.getEndAt() != null) s.setEndAt(parseDateTimeSafe(request.getEndAt()));
    }

    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }

    // 시간대나 소수 초가 포함되어도 앞의 초 단위 시각만 사용한다.
    private LocalDateTime parseDateTimeSafe(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) return null;
        String cleaned = dateTimeStr.length() >= 19 ? dateTimeStr.substring(0, 19) : dateTimeStr;
        return LocalDateTime.parse(cleaned);
    }
}