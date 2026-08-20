package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import memo.example.demo.domain.Notification;
import java.time.LocalDateTime;

/**
 * 사용자 알림 목록에 유형, 연결 대상과 읽음 상태를 반환한다.
 */
@Getter
@Builder
public class NotificationResponseDto {
    private Long notificationId;
    private String type;
    // 알림 유형에 따라 친구 요청 또는 팀 공지 등의 연결 대상 ID를 의미한다.
    private Long targetId;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponseDto from(Notification notification) {
        return NotificationResponseDto.builder()
                .notificationId(notification.getNotificationId())
                .type(notification.getType() != null ? notification.getType().name() : null)
                .targetId(notification.getTargetId())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}