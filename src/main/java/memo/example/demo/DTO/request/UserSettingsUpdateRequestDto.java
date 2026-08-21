package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 화면 표시, 알림 수신과 2단계 인증 관련 사용자 설정을 변경한다.
 */
@Getter
@NoArgsConstructor
public class UserSettingsUpdateRequestDto {
    private String timezone;
    private String dateFormat;
    private String language;
    private Boolean use2fa;
    private Boolean allowPush;
    private Boolean allowEvent;
    private String twoFactorMethod; // PHONE 또는 EMAIL
}