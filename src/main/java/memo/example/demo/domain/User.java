package memo.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 일반·Google 사용자 계정, 프로필, 화면 설정과 2단계 인증 상태를 저장한다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "login_id", length = 55, nullable = false, unique = true)
    private String loginId;

    @Column(name = "email", length = 55, unique = true)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "name", length = 55, nullable = false)
    private String name;

    @Column(name = "nickname", length = 10, nullable = false)
    private String nickname;

    @Column(name = "phone_number", length = 20, unique = true)
    private String phoneNumber;

    // 일반 계정은 LOCAL, Google 로그인 계정은 GOOGLE 값으로 로그인 방식을 구분한다.
    @Column(name = "provider", length = 20, nullable = false)
    private String provider;

    // Google 로그인 계정을 구분하기 위해 전달받은 외부 계정의 고유 식별값을 저장한다.
    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(name = "profile_image_url", length = 1000)
    private String profileImageUrl;

    @Column(name = "timezone", length = 50, nullable = false)
    @Builder.Default
    private String timezone = "Asia/Seoul";

    @Column(name = "date_format", length = 20, nullable = false)
    @Builder.Default
    private String dateFormat = "YYYY-MM-DD";

    @Column(name = "language", length = 20, nullable = false)
    @Builder.Default
    private String language = "ko-KR";

    @Column(name = "use_2fa", nullable = false)
    @Builder.Default
    private Boolean use2fa = false;

    // 2FA 인증 코드를 받을 수단으로 PHONE 또는 EMAIL을 사용한다.
    @Column(name = "two_factor_method", length = 10)
    @Builder.Default
    private String twoFactorMethod = "PHONE";

    @Column(name = "two_factor_secret", length = 255)
    private String twoFactorSecret;

    @Column(name = "allow_push", nullable = false)
    @Builder.Default
    private Boolean allowPush = true;

    @Column(name = "allow_event", nullable = false)
    @Builder.Default
    private Boolean allowEvent = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    // 회원 탈퇴 시 실제 레코드를 삭제하지 않고 탈퇴 시각을 기록한다.
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}