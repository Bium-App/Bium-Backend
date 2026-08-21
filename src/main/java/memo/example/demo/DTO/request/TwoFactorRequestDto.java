package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 2단계 인증 코드 생성·검증 또는 2FA 활성화 요청에 필요한 값을 전달한다.
 */
@Getter
@NoArgsConstructor
public class TwoFactorRequestDto {

    private String action; // SEND는 코드 생성, VERIFY는 코드 검증, SETUP은 2FA 활성화를 요청한다.

    private String method; // 인증 대상을 PHONE 또는 EMAIL로 구분한다.

    private String phoneNumber;
    private String email;
    private String code;
}