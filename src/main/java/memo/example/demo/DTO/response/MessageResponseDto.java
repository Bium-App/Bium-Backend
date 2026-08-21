package memo.example.demo.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 데이터 본문 없이 처리 결과 메시지만 반환하는 API에서 사용한다.
 */
@Getter
@AllArgsConstructor
public class MessageResponseDto {
    private String message;
}