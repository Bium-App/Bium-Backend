package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 메모에 연결해 저장한 이미지 URL 정보와 식별자를 반환한다.
 */
@Getter
@Builder
public class MemoImageResponseDto {
    private Long imageId;
    private String imageUrl;
}
