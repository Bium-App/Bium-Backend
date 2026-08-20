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

    public SearchResponseDto globalSearch(String keyword) {
        List<MemoResponseDto> memos = memoRepository.searchByKeyword(keyword).stream()
                .map(MemoResponseDto::from).collect(Collectors.toList());
        List<TeamTodoResponseDto> todos = teamTodoRepository.searchByKeyword(keyword).stream()
                .map(TeamTodoResponseDto::from).collect(Collectors.toList());
        List<ScheduleResponseDto> schedules = scheduleRepository.searchByKeyword(keyword).stream()
                .map(ScheduleResponseDto::from).collect(Collectors.toList());

        return SearchResponseDto.builder()
                .memos(memos)
                .notices(List.of())
                .todos(todos)
                .schedules(schedules)
                .build();
    }
}