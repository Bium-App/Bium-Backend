package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import memo.example.demo.domain.Memo;
import memo.example.demo.domain.MemoImage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 메모의 본문, 보관 상태, 만료 정보와 연결된 이미지 목록을 반환한다.
 */
@Getter
@Builder
public class MemoResponseDto {
    private Long memoId;
    private Long userId;
    // 값이 없으면 개인 메모, 값이 있으면 해당 TeamSpace의 메모를 의미한다.
    private Long teamSpaceId;
    // TRASH 이동 여부와 별개로 기존 FIRE 또는 ICE 상태를 반환한다.
    private String status;
    private String title;
    private String content;
    // 글자 서식이 포함된 메모 본문을 반환한다.
    private String richContent;
    // 중요 메모를 상단에 고정할지 여부를 반환한다.
    private Boolean isPinned;
    // 값이 있으면 자동 TRASH 처리의 기준 시각을 의미한다.
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 메모에 연결된 이미지 URL 정보 목록이다.
    private List<MemoImageResponseDto> images;

    public static MemoResponseDto from(Memo memo) {
        return from(memo, new ArrayList<>());
    }

    public static MemoResponseDto from(Memo memo, List<MemoImage> images) {
        return MemoResponseDto.builder()
                .memoId(memo.getMemoId())
                .userId(memo.getUser() != null ? memo.getUser().getUserId() : null)
                .teamSpaceId(memo.getTeamSpace() != null ? memo.getTeamSpace().getTeamSpaceId() : null)
                .status(memo.getStatus() != null ? memo.getStatus().name() : null)
                .title(memo.getMTitle())
                .content(memo.getMContent())
                .richContent(memo.getMRichContent())
                .isPinned(memo.getIsPinned())
                .expiredAt(memo.getExpiredAt())
                .createdAt(memo.getCreatedAt())
                .updatedAt(memo.getUpdatedAt())
                .images(images != null ? images.stream()
                        .map(img -> MemoImageResponseDto.builder()
                                .imageId(img.getImageId())
                                .imageUrl(img.getImageUrl())
                                .build())
                        .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }
}