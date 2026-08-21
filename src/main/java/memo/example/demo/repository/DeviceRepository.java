package memo.example.demo.repository;

import memo.example.demo.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

/**
 * 로그인 기기 정보를 저장하고 Refresh Token 또는 사용자 기준으로 기기 세션을 조회·삭제한다.
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByRefreshToken(String refreshToken);

    List<Device> findByUser_UserId(Long userId);

    // 전체 로그아웃 시 해당 사용자의 모든 기기 로그인 세션을 삭제한다.
    void deleteByUser_UserId(Long userId);
}
