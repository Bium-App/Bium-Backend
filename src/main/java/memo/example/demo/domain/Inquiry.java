package memo.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 사용자 문의 내용과 첨부 URL, 답변 및 처리 상태를 저장한다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "inquiry")
public class Inquiry {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long inquiryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private InquiryType type;

    // 답변 전에는 WAITING, 답변 저장 후에는 ANSWERED 상태를 사용한다.
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private InquiryStatus status = InquiryStatus.WAITING;

    @Column(name = "i_title", length = 55, nullable = false)
    private String title;

    @Lob
    @Column(name = "i_content", nullable = false)
    private String content;

    @Column(name = "attachment_url", length = 1000)
    private String attachmentUrl;

    @Lob
    @Column(name = "response")
    private String response;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum InquiryType {
        ONE_ON_ONE, SUGGESTION
    }

    public enum InquiryStatus {
        WAITING, ANSWERED
    }
}