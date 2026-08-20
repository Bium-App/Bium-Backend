package memo.example.demo.DTO.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import memo.example.demo.domain.TeamTodo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * TeamSpace 할 일의 마감일, 완료 여부와 푸시 알림 설정값을 반환한다.
 */
@Getter
@Builder
public class TeamTodoResponseDto {
    private Long todoId;
    private Long teamSpaceId;
    private Long userId;
    private String title;
    private String content;

    // 날짜 값은 시간대 변환 없이 yyyy-MM-dd 형식으로 반환한다.
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    // 실제 발송 결과가 아니라 푸시 알림 사용 여부 설정값이다.
    private Boolean sendPush;
    private Boolean isChecked;

    // 수정 시각은 yyyy-MM-dd'T'HH:mm:ss 형식으로 반환한다.
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;

    public static TeamTodoResponseDto from(TeamTodo teamTodo) {
        return TeamTodoResponseDto.builder()
                .todoId(teamTodo.getTodoId())
                .teamSpaceId(teamTodo.getTeamSpace() != null ? teamTodo.getTeamSpace().getTeamSpaceId() : null)
                .userId(teamTodo.getUser() != null ? teamTodo.getUser().getUserId() : null)
                .title(teamTodo.getTitle())
                .content(teamTodo.getContent())
                .dueDate(teamTodo.getDueDate())
                .sendPush(teamTodo.getSendPush())
                .isChecked(teamTodo.getIsChecked())
                .updatedAt(teamTodo.getUpdatedAt() != null ? teamTodo.getUpdatedAt() : teamTodo.getCreatedAt())
                .build();
    }
}