package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import memo.example.demo.domain.ServiceNotice;
import java.time.LocalDateTime;

/**
 * 서비스 전체 사용자에게 공개되는 운영 공지의 제목과 내용을 반환한다.
 */
@Getter
@Builder
public class ServiceNoticeResponseDto {
    private Long noticeId;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public static ServiceNoticeResponseDto from(ServiceNotice notice) {
        return ServiceNoticeResponseDto.builder()
                .noticeId(notice.getNoticeId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}