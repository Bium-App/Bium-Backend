package memo.example.demo.Exception;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * API 오류 코드와 메시지 및 선택적 필드별 검증 오류를 반환한다.
 */
@Getter
@Builder
public class ErrorResponse {
    private String code;
    private String message;
    private Map<String, String> fieldErrors; // 유효성 검사 실패 시 필드별 에러 내용
}