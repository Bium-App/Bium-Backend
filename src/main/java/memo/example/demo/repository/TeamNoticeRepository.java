package memo.example.demo.repository;

import memo.example.demo.domain.TeamNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * TeamSpace 공지 정보를 저장하고 팀 공간별 공지 목록을 조회한다.
 */
public interface TeamNoticeRepository extends JpaRepository<TeamNotice, Long> {
    List<TeamNotice> findByTeamSpace_TeamSpaceId(Long teamSpaceId);
}