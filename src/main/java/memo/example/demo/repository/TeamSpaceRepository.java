package memo.example.demo.repository;

import memo.example.demo.domain.TeamSpace;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TeamSpace 정보를 저장하고 팀 공간의 생성·조회·수정·삭제에 필요한 데이터 접근을 담당한다.
 */
public interface TeamSpaceRepository extends JpaRepository<TeamSpace, Long> {
}