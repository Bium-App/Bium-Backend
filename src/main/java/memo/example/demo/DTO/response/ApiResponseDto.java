package memo.example.demo.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API 처리 결과 메시지와 선택적 응답 데이터를 공통 형식으로 반환한다.
 */
@Getter
@AllArgsConstructor
public class ApiResponseDto<T> {
    private String message;
    private T data;

    // 데이터가 있는 성공 응답
    public static <T> ApiResponseDto<T> success(String message, T data) {
        return new ApiResponseDto<>(message, data);
    }

    // 데이터가 없는 단순 성공 응답 (예: 삭제 완료)
    public static <T> ApiResponseDto<T> success(String message) {
        return new ApiResponseDto<>(message, null);
    }
}