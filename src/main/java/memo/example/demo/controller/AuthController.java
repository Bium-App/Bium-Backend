package memo.example.demo.controller;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.*;
import memo.example.demo.DTO.response.LoginResponseDto;
import memo.example.demo.DTO.response.MessageResponseDto;
import memo.example.demo.config.jwt.LoginUser;
import memo.example.demo.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 회원가입, 일반·Google 로그인, 토큰 갱신, 로그아웃과 2단계 인증 API 요청을 처리한다.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequestDto request) {
        Long userId = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("userId", userId));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Google 로그인 후 전달받은 사용자 정보로 서비스의 소셜 로그인 요청을 처리한다.
    @PostMapping("/social-login")
    public ResponseEntity<?> socialLogin(@RequestBody SocialLoginRequestDto request) {
        return ResponseEntity.ok(authService.socialLogin(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequestDto request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponseDto> logout(
            @LoginUser Long userId,
            @RequestParam(name = "type", defaultValue = "CURRENT") String type,
            @RequestBody(required = false) LogoutRequestDto request) {

        String refreshToken = request != null ? request.getRefreshToken() : null;
        authService.logout(userId, type, refreshToken);

        return ResponseEntity.ok(new MessageResponseDto("로그아웃 완료"));
    }

    @PostMapping("/find")
    public ResponseEntity<?> findIdOrPw(@RequestBody FindIdPwRequestDto request) {
        return ResponseEntity.ok(Map.of("loginId", authService.findLoginId(request)));
    }

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody VerifyPasswordRequestDto request) {
        return ResponseEntity.ok(Map.of("isMatched", true));
    }

    @PostMapping("/2fa")
    public ResponseEntity<?> handle2FA(@RequestBody TwoFactorRequestDto request) {
        Object result = authService.handle2FA(request);
        if (result instanceof LoginResponseDto) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.ok(new MessageResponseDto("2FA 인증 번호가 전송되었습니다."));
    }
}
