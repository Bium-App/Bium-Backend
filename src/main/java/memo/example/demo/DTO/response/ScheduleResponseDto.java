package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import memo.example.demo.domain.Schedule;
import java.time.LocalDateTime;

/**
 * 개인 또는 TeamSpace 일정의 제목과 시작·종료 시각을 반환한다.
 */
@Getter
@Builder
public class ScheduleResponseDto {
    private Long scheduleId;
    // 값이 없으면 개인 일정, 값이 있으면 해당 TeamSpace 일정을 의미한다.
    private Long teamSpaceId;
    private String title;
    private String content;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public static ScheduleResponseDto from(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .scheduleId(schedule.getScheduleId())
                .teamSpaceId(schedule.getTeamSpace() != null ? schedule.getTeamSpace().getTeamSpaceId() : null)
                .title(schedule.getSTitle())
                .content(schedule.getSContent())
                .startAt(schedule.getStartAt())
                .endAt(schedule.getEndAt())
                .build();
    }
}