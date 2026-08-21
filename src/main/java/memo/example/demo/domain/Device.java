package memo.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 로그인 기기별 Refresh Token과 세션 만료 시각을 저장한다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "device")
public class Device {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 로그인 세션을 구분하기 위한 기기 이름을 저장한다.
    @Column(name = "device_name", length = 100)
    private String deviceName;

    // 로그인 시 발급한 Refresh Token을 기기별 세션에 저장한다.
    @Column(name = "refresh_token", length = 1000, nullable = false)
    private String refreshToken;

    // 해당 기기의 로그인 세션이 만료되는 시각을 저장한다.
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}