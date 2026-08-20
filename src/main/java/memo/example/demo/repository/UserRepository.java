package memo.example.demo.repository;

import memo.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 계정 정보를 저장하고 로그인 아이디·이메일·전화번호 또는 검색어 기준으로 사용자를 조회한다.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);

    // 닉네임 또는 로그인 아이디에 검색어가 포함된 사용자를 대소문자 구분 없이 찾는다.
    @Query("SELECT u FROM User u WHERE LOWER(u.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.loginId) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchByKeyword(@Param("keyword") String keyword);
}