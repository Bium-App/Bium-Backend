package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 로그인 기기 목록에 표시할 기기 식별 정보와 세션 생성 시각을 반환한다.
 */
@Getter
@Builder
public class DeviceResponseDto {
    private Long deviceId;
    private String deviceName;
    private LocalDateTime lastLoginAt;
}