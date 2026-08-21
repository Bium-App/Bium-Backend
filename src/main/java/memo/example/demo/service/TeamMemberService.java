package memo.example.demo.service;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.response.TeamMemberResponseDto;
import memo.example.demo.domain.TeamMember;
import memo.example.demo.domain.TeamMember.Role;
import memo.example.demo.domain.TeamSpace;
import memo.example.demo.domain.User;
import memo.example.demo.repository.TeamMemberRepository;
import memo.example.demo.repository.TeamSpaceRepository;
import memo.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 팀 스페이스 구성원의 등록, LEADER/MEMBER 역할 변경과 제거를 관리한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamSpaceRepository teamSpaceRepository;
    private final UserRepository userRepository;
    private final TeamAccessService teamAccessService;

    public void addMember(Long currentUserId, Long teamSpaceId, Long userId, Role role) {
        // 요청으로 전달된 LEADER 또는 MEMBER 역할을 사용해 TeamSpace 구성원을 등록한다.
        teamAccessService.requireLeader(teamSpaceId, currentUserId);
        TeamSpace teamSpace = teamAccessService.requireActiveTeamSpace(teamSpaceId);
        User user = userRepository.findById(userId).orElseThrow();
        if (user.getDeletedAt() != null) {
            throw new IllegalArgumentException("탈퇴한 사용자는 팀원으로 추가할 수 없습니다.");
        }
        if (teamMemberRepository.existsByTeamSpace_TeamSpaceIdAndUser_UserId(teamSpaceId, userId)) {
            throw new IllegalStateException("이미 팀 스페이스에 참여 중인 사용자입니다.");
        }
        TeamMember teamMember = TeamMember.builder()
                .teamSpace(teamSpace)
                .user(user)
                .role(role)
                .build();
        teamMemberRepository.save(teamMember);
    }

    @Transactional(readOnly = true)
    public List<TeamMemberResponseDto> getTeamMembers(Long currentUserId, Long teamSpaceId) {
        teamAccessService.requireMember(teamSpaceId, currentUserId);
        return teamMemberRepository.findByTeamSpace_TeamSpaceId(teamSpaceId).stream()
                .map(TeamMemberResponseDto::from)
                .collect(Collectors.toList());
    }

    public void changeRole(Long currentUserId, Long teamMemberId, Role role) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId).orElseThrow();
        teamAccessService.requireLeader(teamMember.getTeamSpace().getTeamSpaceId(), currentUserId);
        teamMember.setRole(role);
    }

    public void removeMember(Long currentUserId, Long teamMemberId) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId).orElseThrow();
        teamAccessService.requireLeader(teamMember.getTeamSpace().getTeamSpaceId(), currentUserId);
        teamMemberRepository.delete(teamMember);
    }
}
