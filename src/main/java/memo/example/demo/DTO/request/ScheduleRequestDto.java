package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 개인 또는 TeamSpace 일정을 생성·수정할 때 제목과 기간을 전달한다.
 */
@Getter
@NoArgsConstructor
public class ScheduleRequestDto {

    // 개인 일정과 TeamSpace 일정을 구분하기 위해 대상 TeamSpace ID를 전달한다.
    private Long teamSpaceId;

    private String title;
    private String content;

    // 일정이 시작되는 시각을 전달한다.
    private String startAt;

    // 일정이 종료되는 시각을 전달한다.
    private String endAt;
}