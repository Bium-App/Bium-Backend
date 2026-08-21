package memo.example.demo.repository;

import memo.example.demo.domain.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * TeamSpace 구성원 정보를 저장하고 팀 공간 또는 사용자 기준으로 소속 정보를 조회한다.
 */
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByTeamSpace_TeamSpaceId(Long teamSpaceId);
    List<TeamMember> findByUser_UserId(Long userId);
    Optional<TeamMember> findByTeamSpace_TeamSpaceIdAndUser_UserId(Long teamSpaceId, Long userId);
    boolean existsByTeamSpace_TeamSpaceIdAndUser_UserId(Long teamSpaceId, Long userId);
}
