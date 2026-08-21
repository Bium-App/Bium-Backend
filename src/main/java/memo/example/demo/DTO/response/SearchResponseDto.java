package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

/**
 * 메모, 팀 할 일과 일정의 통합 검색 결과를 종류별 목록으로 반환한다.
 */
@Getter
@Builder
public class SearchResponseDto {
    private List<MemoResponseDto> memos;
    // 현재 검색 로직에서는 공지를 조회하지 않아 빈 목록으로 반환한다.
    private List<TeamNoticeResponseDto> notices;
    private List<TeamTodoResponseDto> todos;
    private List<ScheduleResponseDto> schedules;
}