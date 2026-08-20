package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 성공 후 인증 토큰과 사용자 및 기기 식별 정보를 반환한다.
 */
@Getter
@Builder
public class LoginResponseDto {
    // API 요청 인증에 사용하는 단기 토큰이다.
    private String accessToken;
    // 로그인 세션 갱신에 사용하는 장기 토큰이다.
    private String refreshToken;
    private Long userId;
    private Long deviceId;
}