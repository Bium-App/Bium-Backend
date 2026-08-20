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

    public void addMember(Long teamSpaceId, Long userId, Role role) {
        // 요청으로 전달된 LEADER 또는 MEMBER 역할을 사용해 TeamSpace 구성원을 등록한다.
        TeamSpace teamSpace = teamSpaceRepository.findById(teamSpaceId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        TeamMember teamMember = TeamMember.builder()
                .teamSpace(teamSpace)
                .user(user)
                .role(role)
                .build();
        teamMemberRepository.save(teamMember);
    }

    @Transactional(readOnly = true)
    public List<TeamMemberResponseDto> getTeamMembers(Long teamSpaceId) {
        return teamMemberRepository.findAll().stream()
                .filter(tm -> tm.getTeamSpace().getTeamSpaceId().equals(teamSpaceId))
                .map(TeamMemberResponseDto::from)
                .collect(Collectors.toList());
    }

    public void changeRole(Long teamMemberId, Role role) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId).orElseThrow();
        teamMember.setRole(role);
    }

    public void removeMember(Long teamMemberId) {
        teamMemberRepository.deleteById(teamMemberId);
    }
}