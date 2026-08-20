package memo.example.demo.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import memo.example.demo.DTO.request.LoginRequestDto;
import memo.example.demo.DTO.request.FindIdPwRequestDto;
import memo.example.demo.DTO.request.SignUpRequestDto;
import memo.example.demo.DTO.request.SocialLoginRequestDto;
import memo.example.demo.DTO.request.TwoFactorRequestDto;
import memo.example.demo.DTO.response.LoginResponseDto;
import memo.example.demo.Exception.ExpiredCodeException;
import memo.example.demo.Exception.InvalidCodeException;
import memo.example.demo.config.jwt.JwtTokenProvider;
import memo.example.demo.domain.Device;
import memo.example.demo.domain.User;
import memo.example.demo.repository.DeviceRepository;
import memo.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 회원가입과 일반·Google 로그인, JWT 세션 갱신·로그아웃 및 2단계 인증 절차를 관리한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    // 인증 대상별 2FA 코드와 검증 상태를 서버 메모리에 임시 보관한다.
    private final Map<String, TwoFactorSession> mfaSessions = new ConcurrentHashMap<>();

    @Getter
    @Setter
    public static class TwoFactorSession {
        private String code;
        private LocalDateTime expiresAt;
        private LocalDateTime lastSentAt;
        private int failureCount;
        private boolean isVerified = false;
    }

    public Long signup(SignUpRequestDto request) {
        // 중복 아이디를 차단하고 비밀번호를 암호화해 일반 사용자 계정을 생성한다.
        if (userRepository.findByLoginId(request.getLoginId()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        User user = User.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .provider(request.getProvider())
                .build();
        return userRepository.save(user).getUserId();
    }

    public LoginResponseDto login(LoginRequestDto request) {
        // 아이디와 비밀번호가 일치하면 토큰을 발급하고 기기별 로그인 세션을 만든다.
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        return createLoginSession(user, request.getDeviceName());
    }

    // Google 로그인 후 전달받은 사용자 정보를 확인해 계정을 연결하거나 새로 생성하고 로그인 세션을 만든다.
    public Map<String, Object> socialLogin(SocialLoginRequestDto request) {
        boolean isNewUser = false;
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        User user;

        // 같은 이메일의 일반 계정이 있으면 Google 식별 정보를 연결해 기존 계정을 그대로 사용한다.
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            if ("LOCAL".equals(user.getProvider())) {
                user.setProvider(request.getProvider());
                user.setProviderId(request.getProviderId());
            }
        } else {
            // 처음 로그인한 Google 사용자는 비밀번호가 없는 소셜 계정으로 새로 등록한다.
            isNewUser = true;

            // 일반 로그인 아이디와 구분할 수 있도록 내부에서 사용할 고유 로그인 아이디를 만든다.
            String uniqueLoginId = request.getProvider().toLowerCase() + "_" + UUID.randomUUID().toString().substring(0, 8);

            user = User.builder()
                    .loginId(uniqueLoginId)
                    .password(null)
                    .name(request.getName() != null ? request.getName() : "소셜회원")
                    .nickname(request.getName() != null ? request.getName() : "소셜회원")
                    .email(request.getEmail())
                    .provider(request.getProvider())
                    .providerId(request.getProviderId())
                    .profileImageUrl(request.getProfileImageUrl())
                    .build();
            userRepository.save(user);
        }

        LoginResponseDto loginResponse = createLoginSession(user, request.getDeviceName());

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", loginResponse.getAccessToken());
        response.put("refreshToken", loginResponse.getRefreshToken());
        response.put("userId", loginResponse.getUserId());
        response.put("deviceId", loginResponse.getDeviceId());
        response.put("isNewUser", isNewUser);

        return response;
    }

    // 일반·Google·2FA 로그인에서 공통으로 Access/Refresh Token과 기기별 로그인 세션을 생성한다.
    private LoginResponseDto createLoginSession(User user, String deviceName) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());
        Device device = Device.builder()
                .user(user)
                .deviceName(deviceName != null ? deviceName : "Unknown Device")
                .refreshToken(refreshToken)
                .expiresAt(LocalDateTime.now().plusDays(14))
                .build();
        deviceRepository.save(device);
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .deviceId(device.getDeviceId())
                .build();
    }

    public LoginResponseDto refreshToken(String refreshToken) {
        Device device = deviceRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));
        if (device.getExpiresAt().isBefore(LocalDateTime.now())) {
            deviceRepository.delete(device);
            throw new IllegalArgumentException("리프레시 토큰이 만료되었습니다.");
        }
        // 유효한 Refresh Token을 사용할 때 Access Token과 Refresh Token을 함께 교체한다.
        String newAccessToken = jwtTokenProvider.createAccessToken(device.getUser().getUserId());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(device.getUser().getUserId());
        device.setRefreshToken(newRefreshToken);
        device.setExpiresAt(LocalDateTime.now().plusDays(14));
        deviceRepository.save(device);
        return LoginResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(device.getUser().getUserId())
                .deviceId(device.getDeviceId())
                .build();
    }

    public void logout(Long userId, String type, String refreshToken) {
        // ALL은 모든 기기 세션을, CURRENT는 전달된 Refresh Token의 본인 세션만 삭제한다.
        if ("ALL".equalsIgnoreCase(type)) {
            deviceRepository.deleteByUser_UserId(userId);
        } else {
            if (refreshToken != null) {
                deviceRepository.findByRefreshToken(refreshToken)
                        .ifPresent(device -> {
                            if (device.getUser().getUserId().equals(userId)) {
                                deviceRepository.delete(device);
                            }
                        });
            }
        }
    }

    @Transactional(readOnly = true)
    public String findLoginId(FindIdPwRequestDto request) {
        // ID 유형의 요청만 허용하고 이메일과 연결된 로그인 아이디를 반환한다.
        if (!"ID".equalsIgnoreCase(request.getType())) {
            throw new IllegalArgumentException("아이디 찾기 요청만 지원합니다.");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("이메일을 입력해 주세요.");
        }

        return userRepository.findByEmail(request.getEmail().trim())
                .map(User::getLoginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 사용자를 찾을 수 없습니다."));
    }

    public Object handle2FA(TwoFactorRequestDto request) {
        String method = request.getMethod() != null ? request.getMethod().toUpperCase() : "PHONE";
        String destination = "EMAIL".equals(method) ? request.getEmail() : request.getPhoneNumber();

        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("인증 대상 정보가 누락되었습니다.");
        }

        if ("SEND".equalsIgnoreCase(request.getAction())) {
            TwoFactorSession session = mfaSessions.getOrDefault(destination, new TwoFactorSession());
            // 같은 인증 대상으로는 1분마다 새 코드를 생성할 수 있고, 생성된 코드는 3분간 유효하다.
            if (session.getLastSentAt() != null && session.getLastSentAt().plusMinutes(1).isAfter(LocalDateTime.now())) {
                throw new IllegalStateException("인증번호는 1분마다 재발송할 수 있습니다.");
            }
            String code = String.format("%06d", new Random().nextInt(1000000));
            session.setCode(code);
            session.setExpiresAt(LocalDateTime.now().plusMinutes(3));
            session.setLastSentAt(LocalDateTime.now());
            session.setFailureCount(0);
            session.setVerified(false);
            mfaSessions.put(destination, session);
            // 현재 구현은 외부 문자·이메일 발송 대신 생성된 인증 코드를 서버 콘솔에 출력한다.
            System.out.println("[" + destination + "] 발송된 2FA 코드: " + code);

            return null;

        } else if ("VERIFY".equalsIgnoreCase(request.getAction())) {
            TwoFactorSession session = mfaSessions.get(destination);
            if (session == null) throw new IllegalArgumentException("인증번호 발송 이력이 없습니다.");
            // 인증 실패가 5회 누적되면 임시 세션을 제거해 새 인증 코드를 생성하도록 한다.
            if (session.getFailureCount() >= 5) {
                mfaSessions.remove(destination);
                throw new IllegalStateException("인증 실패 횟수(5회)를 초과했습니다. 재발송해 주세요.");
            }
            if (session.getExpiresAt().isBefore(LocalDateTime.now())) throw new ExpiredCodeException("인증번호가 만료되었습니다.");
            if (!session.getCode().equals(request.getCode())) {
                session.setFailureCount(session.getFailureCount() + 1);
                throw new InvalidCodeException("잘못된 인증번호입니다. (남은 횟수: " + (5 - session.getFailureCount()) + ")");
            }

            session.setVerified(true);

            User user = "EMAIL".equals(method)
                    ? userRepository.findByEmail(destination).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."))
                    : userRepository.findByPhoneNumber(destination).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            return createLoginSession(user, "2FA Verified Device");

        } else if ("SETUP".equalsIgnoreCase(request.getAction())) {
            TwoFactorSession session = mfaSessions.get(destination);
            // 코드 검증이 완료된 임시 세션이 있어야 2단계 인증 설정을 활성화한다.
            if (session == null || !session.isVerified()) {
                throw new SecurityException("인증번호 검증(VERIFY)이 선행되어야 합니다.");
            }

            User user = "EMAIL".equals(method)
                    ? userRepository.findByEmail(destination).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."))
                    : userRepository.findByPhoneNumber(destination).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            user.setUse2fa(true);
            user.setTwoFactorMethod(method);

            mfaSessions.remove(destination);
            return null;
        } else {
            throw new IllegalArgumentException("지원하지 않는 인증 액션입니다.");
        }
    }
}