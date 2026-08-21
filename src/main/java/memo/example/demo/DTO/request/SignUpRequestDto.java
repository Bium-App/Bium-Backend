package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입에 필요한 로그인 정보와 기본 사용자 정보를 전달한다.
 */
@Getter
@NoArgsConstructor
public class SignUpRequestDto {
    private String loginId;
    private String password;
    private String name;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String provider; // 일반 회원가입은 LOCAL 값을 사용한다.
}