package memo.example.demo.repository;

import memo.example.demo.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 사용자에게 생성된 알림 정보를 저장하고 최신 알림부터 조회한다.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
}