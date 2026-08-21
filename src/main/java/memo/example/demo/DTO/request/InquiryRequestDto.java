package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 문의를 접수할 때 문의 유형, 내용과 선택적 첨부 URL을 전달한다.
 */
@Getter
@NoArgsConstructor
public class InquiryRequestDto {
    private String type; // ONE_ON_ONE, SUGGESTION
    private String title;
    private String content;
    private String attachmentUrl; // S3 업로드된 이미지 URL (선택적)
}