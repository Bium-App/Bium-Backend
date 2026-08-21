package memo.example.demo.service;

import lombok.RequiredArgsConstructor;
import memo.example.demo.domain.TeamMember;
import memo.example.demo.domain.TeamSpace;
import memo.example.demo.repository.TeamMemberRepository;
import memo.example.demo.repository.TeamSpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 삭제되지 않은 TeamSpace에 대한 현재 사용자의 멤버십과 LEADER 권한을 확인한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamAccessService {

    private final TeamSpaceRepository teamSpaceRepository;
    private final TeamMemberRepository teamMemberRepository;

    public TeamSpace requireActiveTeamSpace(Long teamSpaceId) {
        TeamSpace teamSpace = teamSpaceRepository.findById(teamSpaceId)
                .orElseThrow(() -> new IllegalArgumentException("팀 스페이스를 찾을 수 없습니다."));
        if (teamSpace.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 팀 스페이스입니다.");
        }
        return teamSpace;
    }

    public TeamMember requireMember(Long teamSpaceId, Long userId) {
        requireActiveTeamSpace(teamSpaceId);
        return teamMemberRepository.findByTeamSpace_TeamSpaceIdAndUser_UserId(teamSpaceId, userId)
                .orElseThrow(() -> new SecurityException("팀 스페이스 멤버만 접근할 수 있습니다."));
    }

    public void requireLeader(Long teamSpaceId, Long userId) {
        TeamMember member = requireMember(teamSpaceId, userId);
        if (member.getRole() != TeamMember.Role.LEADER) {
            throw new SecurityException("팀 스페이스 LEADER 권한이 필요합니다.");
        }
    }

    public boolean canAccess(Long teamSpaceId, Long userId) {
        return teamSpaceRepository.findById(teamSpaceId)
                .filter(teamSpace -> teamSpace.getDeletedAt() == null)
                .isPresent()
                && teamMemberRepository.existsByTeamSpace_TeamSpaceIdAndUser_UserId(teamSpaceId, userId);
    }
}
