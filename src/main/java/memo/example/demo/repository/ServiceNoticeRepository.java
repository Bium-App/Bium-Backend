package memo.example.demo.repository;

import memo.example.demo.domain.ServiceNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 서비스 전체에 제공되는 공지 정보를 저장하고 최신 공지부터 조회한다.
 */
public interface ServiceNoticeRepository extends JpaRepository<ServiceNotice, Long> {
    List<ServiceNotice> findAllByOrderByCreatedAtDesc();
}