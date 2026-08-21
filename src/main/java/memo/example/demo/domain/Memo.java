package memo.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 메모의 FIRE/ICE 상태와 Rich Content를 저장하며 TRASH 여부는 deletedAt으로 관리한다.
 */
@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(name = "memo")
public class Memo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_id")
    private Long memoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_space_id")
    private TeamSpace teamSpace;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemoStatus status;

    @Column(name = "m_title", length = 55, nullable = false)
    private String mTitle;

    @Column(name = "m_content", columnDefinition = "TEXT", nullable = false)
    private String mContent;

    @Column(name = "m_rich_content", columnDefinition = "TEXT")
    // 글자 서식이 포함된 메모 본문을 저장한다.
    private String mRichContent;

    @Column(name = "is_pinned", nullable = false)
    @Builder.Default
    // 중요한 메모를 목록 상단에 고정할지 여부를 저장한다.
    private Boolean isPinned = false;

    @Column(name = "expired_at")
    // 메모가 자동으로 TRASH로 이동할 기준 시각을 저장한다.
    private LocalDateTime expiredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    // 값이 존재하면 메모가 TRASH에 있는 상태로 판단한다.
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum MemoStatus {
        FIRE, ICE
    }
}