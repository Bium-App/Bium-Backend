package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 일반 로그인에 사용할 아이디와 비밀번호 및 로그인 기기 이름을 전달한다.
 */
@Getter
@NoArgsConstructor
public class LoginRequestDto {
    private String loginId;
    private String password;
    private String deviceName; // 예: iPhone 16 Pro
}