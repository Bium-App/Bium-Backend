package memo.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Google 공개키로 ID token을 검증하고 신뢰할 수 있는 계정 식별 정보만 반환한다.
 */
@Service
public class GoogleIdTokenService {

    private static final String GOOGLE_JWK_SET_URI = "https://www.googleapis.com/oauth2/v3/certs";
    private static final List<String> GOOGLE_ISSUERS = List.of("https://accounts.google.com", "accounts.google.com");

    private final String clientId;
    private final JwtDecoder jwtDecoder;

    public GoogleIdTokenService(@Value("${google.oauth2.client-id:}") String clientId) {
        this.clientId = clientId;

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(GOOGLE_JWK_SET_URI).build();
        OAuth2TokenValidator<Jwt> issuerValidator = jwt -> jwt.getIssuer() != null
                && GOOGLE_ISSUERS.contains(jwt.getIssuer().toString())
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Google issuer가 일치하지 않습니다.", null));
        OAuth2TokenValidator<Jwt> audienceValidator = jwt -> StringUtils.hasText(this.clientId)
                && jwt.getAudience().contains(this.clientId)
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Google audience가 일치하지 않습니다.", null));
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(), issuerValidator, audienceValidator));
        this.jwtDecoder = decoder;
    }

    public VerifiedGoogleUser verify(String idToken) {
        if (!StringUtils.hasText(clientId)) {
            throw new IllegalStateException("google.oauth2.client-id 설정이 필요합니다.");
        }
        if (!StringUtils.hasText(idToken)) {
            throw new IllegalArgumentException("Google ID token이 필요합니다.");
        }

        try {
            Jwt jwt = jwtDecoder.decode(idToken);
            String subject = jwt.getSubject();
            String email = jwt.getClaimAsString("email");
            Boolean emailVerified = jwt.getClaimAsBoolean("email_verified");
            if (!StringUtils.hasText(subject) || !StringUtils.hasText(email) || !Boolean.TRUE.equals(emailVerified)) {
                throw new IllegalArgumentException("검증된 Google 계정 이메일을 확인할 수 없습니다.");
            }
            return new VerifiedGoogleUser(
                    subject,
                    email,
                    jwt.getClaimAsString("name"),
                    jwt.getClaimAsString("picture"));
        } catch (JwtException ex) {
            throw new IllegalArgumentException("유효하지 않은 Google ID token입니다.");
        }
    }

    public record VerifiedGoogleUser(String subject, String email, String name, String profileImageUrl) {
    }
}
