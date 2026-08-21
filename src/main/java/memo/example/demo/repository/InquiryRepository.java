package memo.example.demo.repository;

import memo.example.demo.domain.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 사용자 문의 정보를 저장하고 사용자별 문의 목록을 조회한다.
 */
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByUser_UserId(Long userId);
}