package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기존 TeamSpace 할 일에서 변경할 내용, 완료 여부와 알림 설정값을 전달한다.
 */
@Getter
@NoArgsConstructor
public class TeamTodoUpdateRequestDto {
    private String title;
    private String content;
    private String dueDate;
    private Boolean isChecked;
    // 실제 발송이 아니라 푸시 알림 사용 여부 설정값을 변경한다.
    private Boolean sendPush;
}