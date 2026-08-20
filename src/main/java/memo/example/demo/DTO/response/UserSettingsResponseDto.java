package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import memo.example.demo.domain.User;

/**
 * 화면 표시, 알림 수신과 2FA 상태 등 현재 사용자 설정을 반환한다.
 */
@Getter
@Builder
public class UserSettingsResponseDto {
    private String timezone;
    private String dateFormat;
    private String language;
    private Boolean use2fa;
    private Boolean allowPush;
    private Boolean allowEvent;

    // 2FA 수단과 마스킹된 인증 대상을 함께 반환한다.
    private String twoFactorMethod;
    private String twoFactorDestination;

    public static UserSettingsResponseDto from(User user, String maskedDestination) {
        return UserSettingsResponseDto.builder()
                .timezone(user.getTimezone())
                .dateFormat(user.getDateFormat())
                .language(user.getLanguage())
                .use2fa(user.getUse2fa())
                .allowPush(user.getAllowPush())
                .allowEvent(user.getAllowEvent())
                .twoFactorMethod(user.getTwoFactorMethod())
                .twoFactorDestination(maskedDestination)
                .build();
    }
}