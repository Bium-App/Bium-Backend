package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import memo.example.demo.domain.Memo;
import java.time.LocalDateTime;

/**
 * TRASH 목록에 표시할 메모 제목, 기존 보관 상태와 삭제 시각을 반환한다.
 */
@Getter
@Builder
public class TrashResponseDto {
    private Long memoId;
    private String title;
    // TRASH에서도 이동 전 FIRE 또는 ICE 상태를 그대로 반환한다.
    private String status;
    // 값이 기록된 시점부터 TRASH 보관 시간이 계산된다.
    private LocalDateTime deletedAt;

    public static TrashResponseDto from(Memo memo) {
        return TrashResponseDto.builder()
                .memoId(memo.getMemoId())
                .title(memo.getMTitle())
                .status(memo.getStatus() != null ? memo.getStatus().name() : null)
                .deletedAt(memo.getDeletedAt())
                .build();
    }
}