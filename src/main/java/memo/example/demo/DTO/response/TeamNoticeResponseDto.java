package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import memo.example.demo.domain.TeamNotice;
import java.time.LocalDateTime;

/**
 * TeamSpace 공지의 내용, 상단 고정 여부와 작성·수정 시각을 반환한다.
 */
@Getter
@Builder
public class TeamNoticeResponseDto {
    private Long noticeId;
    private String title;
    private String content;
    private Boolean isPinned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TeamNoticeResponseDto from(TeamNotice notice) {
        return TeamNoticeResponseDto.builder()
                .noticeId(notice.getNoticeId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .isPinned(notice.getIsPinned())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt() != null ? notice.getUpdatedAt() : notice.getCreatedAt())
                .build();
    }
}