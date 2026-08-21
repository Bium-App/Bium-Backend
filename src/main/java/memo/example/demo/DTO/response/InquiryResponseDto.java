package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import memo.example.demo.domain.Inquiry;
import java.time.LocalDateTime;

/**
 * 접수된 문의의 내용, 처리 상태와 등록된 답변을 반환한다.
 */
@Getter
@Builder
public class InquiryResponseDto {
    private Long inquiryId;
    private String type;
    private String status;
    private String title;
    private String content;
    private String attachmentUrl;
    private String response;
    private LocalDateTime createdAt;

    public static InquiryResponseDto from(Inquiry inquiry) {
        return InquiryResponseDto.builder()
                .inquiryId(inquiry.getInquiryId())
                .type(inquiry.getType() != null ? inquiry.getType().name() : null)
                .status(inquiry.getStatus() != null ? inquiry.getStatus().name() : null)
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .attachmentUrl(inquiry.getAttachmentUrl())
                .response(inquiry.getResponse())
                .createdAt(inquiry.getCreatedAt())
                .build();
    }
}