package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TeamSpace 할 일을 생성할 때 내용, 마감일과 푸시 알림 설정값을 전달한다.
 */
@Getter
@NoArgsConstructor
public class TeamTodoRequestDto {
    private String title;
    private String content;
    private String dueDate; // (예: "2026-07-30")
    private Boolean sendPush; // 푸시 알림 사용 여부 설정값
}