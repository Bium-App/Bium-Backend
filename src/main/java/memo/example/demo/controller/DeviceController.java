package memo.example.demo.controller;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.response.DeviceResponseDto;
import memo.example.demo.DTO.response.MessageResponseDto;
import memo.example.demo.config.jwt.LoginUser;
import memo.example.demo.domain.Device;
import memo.example.demo.repository.DeviceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 로그인 기기 목록 조회와 지정된 기기 세션 삭제 API 요청을 처리한다.
 */
@RestController
@RequestMapping("/api/auth/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceRepository deviceRepository;

    @GetMapping
    public ResponseEntity<?> getDevices(@LoginUser Long userId) {
        List<DeviceResponseDto> devices = deviceRepository.findByUser_UserId(userId).stream()
                .map(d -> DeviceResponseDto.builder()
                        .deviceId(d.getDeviceId())
                        .deviceName(d.getDeviceName() != null ? d.getDeviceName() : "알 수 없는 기기")
                        .lastLoginAt(d.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(devices);
    }

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<MessageResponseDto> logoutDevice(
            @LoginUser Long userId,
            @PathVariable Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("기기를 찾을 수 없습니다."));
        if (!device.getUser().getUserId().equals(userId)) {
            throw new SecurityException("다른 사용자의 기기입니다.");
        }
        deviceRepository.delete(device);
        return ResponseEntity.ok(new MessageResponseDto("해당 기기에서 로그아웃 되었습니다."));
    }
}
