package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Google 로그인 후 전달받은 사용자 정보와 현재 기기 정보를 서비스 로그인에 사용한다.
 */
@Getter
@NoArgsConstructor
public class SocialLoginRequestDto {

    // 현재 앱의 소셜 로그인 제공자는 GOOGLE을 사용한다.
    private String provider;

    // Google이 서명한 ID token이며 서버는 이 값에서 계정 식별값과 이메일을 확인한다.
    private String idToken;

    // Google 계정을 구분하기 위한 고유 식별값이다.
    private String providerId;

    private String email;
    private String name;
    private String profileImageUrl;

    // 로그인 세션을 생성할 기기의 이름을 전달한다.
    private String deviceName;
}
