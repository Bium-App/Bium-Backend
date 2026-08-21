package memo.example.demo.repository;

import memo.example.demo.domain.TeamFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * TeamSpace에 등록된 파일 정보를 저장하고 팀 공간별 파일 목록을 조회한다.
 */
public interface TeamFileRepository extends JpaRepository<TeamFile, Long> {
    List<TeamFile> findByTeamSpace_TeamSpaceId(Long teamSpaceId);
}