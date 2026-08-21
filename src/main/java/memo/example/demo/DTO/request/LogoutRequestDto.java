package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 현재 로그인 기기 세션을 종료할 때 해당 Refresh Token을 전달한다.
 */
@Getter
@NoArgsConstructor
public class LogoutRequestDto {
    // 현재 기기에 저장된 로그인 세션을 찾는 데 사용한다.
    private String refreshToken;
}