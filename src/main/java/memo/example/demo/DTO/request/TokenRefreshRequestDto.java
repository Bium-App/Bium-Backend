package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 세션을 갱신하고 새 토큰을 발급받기 위해 Refresh Token을 전달한다.
 */
@Getter
@NoArgsConstructor
public class TokenRefreshRequestDto {
    // 저장된 기기 세션을 찾고 만료 여부를 확인하는 데 사용한다.
    private String refreshToken;
}