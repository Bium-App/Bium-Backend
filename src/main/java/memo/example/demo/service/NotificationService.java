package memo.example.demo.service;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.response.NotificationResponseDto;
import memo.example.demo.domain.Notification;
import memo.example.demo.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 알림을 최신순으로 조회하고 읽음 상태와 삭제를 관리한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getUserNotifications(Long userId) {
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = requireOwnedNotification(userId, notificationId);
        notification.setIsRead(true);
    }

    public void deleteNotification(Long userId, Long notificationId) {
        notificationRepository.delete(requireOwnedNotification(userId, notificationId));
    }

    private Notification requireOwnedNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        if (!notification.getUser().getUserId().equals(userId)) {
            throw new SecurityException("다른 사용자의 알림입니다.");
        }
        return notification;
    }
}
