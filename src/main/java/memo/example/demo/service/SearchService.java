package memo.example.demo.service;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.response.*;
import memo.example.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 메모, 팀 할 일과 일정 검색 결과를 하나의 응답으로 구성한다.
 * 공지 검색 결과는 현재 빈 목록으로 반환한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {
    private final MemoRepository memoRepository;
    private final TeamTodoRepository teamTodoRepository;
    private final ScheduleRepository scheduleRepository;
    private final TeamAccessService teamAccessService;

    public SearchResponseDto globalSearch(Long userId, String keyword) {
        List<MemoResponseDto> memos = memoRepository.searchByKeyword(keyword).stream()
                .filter(memo -> memo.getDeletedAt() == null)
                .filter(memo -> memo.getTeamSpace() == null
                        ? memo.getUser().getUserId().equals(userId)
                        : teamAccessService.canAccess(memo.getTeamSpace().getTeamSpaceId(), userId))
                .map(MemoResponseDto::from).collect(Collectors.toList());
        List<TeamTodoResponseDto> todos = teamTodoRepository.searchByKeyword(keyword).stream()
                .filter(todo -> teamAccessService.canAccess(todo.getTeamSpace().getTeamSpaceId(), userId))
                .map(TeamTodoResponseDto::from).collect(Collectors.toList());
        List<ScheduleResponseDto> schedules = scheduleRepository.searchByKeyword(keyword).stream()
                .filter(schedule -> schedule.getTeamSpace() == null
                        ? schedule.getUser().getUserId().equals(userId)
                        : teamAccessService.canAccess(schedule.getTeamSpace().getTeamSpaceId(), userId))
                .map(ScheduleResponseDto::from).collect(Collectors.toList());

        return SearchResponseDto.builder()
                .memos(memos)
                .notices(List.of())
                .todos(todos)
                .schedules(schedules)
                .build();
    }
}
