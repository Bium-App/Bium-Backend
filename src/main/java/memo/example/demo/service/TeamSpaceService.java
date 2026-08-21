package memo.example.demo.service;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.TeamSpaceCreateRequestDto;
import memo.example.demo.DTO.request.TeamSpaceRequestDto;
import memo.example.demo.DTO.response.TeamSpaceResponseDto;
import memo.example.demo.domain.TeamMember;
import memo.example.demo.domain.TeamSpace;
import memo.example.demo.domain.User;
import memo.example.demo.repository.TeamMemberRepository;
import memo.example.demo.repository.TeamSpaceRepository;
import memo.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TeamSpace 생성, 조회, 이름 변경과 soft delete 상태를 관리한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TeamSpaceService {

    private final TeamSpaceRepository teamSpaceRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final TeamAccessService teamAccessService;

    public Long createTeamSpace(Long userId, TeamSpaceCreateRequestDto request) {
        // TeamSpace 생성자를 LEADER 역할의 첫 번째 구성원으로 등록한다.
        TeamSpace teamSpace = TeamSpace.builder()
                .name(request.getName())
                .build();
        teamSpace = teamSpaceRepository.save(teamSpace);
        User user = userRepository.findById(userId).orElseThrow();
        TeamMember teamMember = TeamMember.builder()
                .teamSpace(teamSpace)
                .user(user)
                .role(TeamMember.Role.LEADER)
                .build();
        teamMemberRepository.save(teamMember);
        return teamSpace.getTeamSpaceId();
    }

    @Transactional(readOnly = true)
    public List<TeamSpaceResponseDto> getMyTeamSpaces(Long userId) {
        return teamMemberRepository.findByUser_UserId(userId).stream()
                // soft delete된 TeamSpace는 참여 목록에서 제외한다.
                .filter(tm -> tm.getTeamSpace().getDeletedAt() == null)
                .map(tm -> TeamSpaceResponseDto.from(tm.getTeamSpace(), teamMemberRepository.findByTeamSpace_TeamSpaceId(tm.getTeamSpace().getTeamSpaceId()).size()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeamSpaceResponseDto getTeamSpace(Long userId, Long teamSpaceId) {
        TeamSpace teamSpace = teamAccessService.requireMember(teamSpaceId, userId).getTeamSpace();

        Integer memberCount = teamMemberRepository.findByTeamSpace_TeamSpaceId(teamSpaceId).size();
        return TeamSpaceResponseDto.from(teamSpace, memberCount);
    }

    public void updateTeamSpaceName(Long userId, Long teamSpaceId, TeamSpaceRequestDto request) {
        teamAccessService.requireLeader(teamSpaceId, userId);
        TeamSpace teamSpace = teamAccessService.requireActiveTeamSpace(teamSpaceId);
        if (request.getName() != null && !request.getName().isBlank()) {
            teamSpace.setName(request.getName());
        }
    }

    public void deleteTeamSpace(Long userId, Long teamSpaceId) {
        // 레코드를 즉시 제거하지 않고 삭제 시각을 기록해 soft delete 처리한다.
        teamAccessService.requireLeader(teamSpaceId, userId);
        TeamSpace teamSpace = teamAccessService.requireActiveTeamSpace(teamSpaceId);
        teamSpace.setDeletedAt(LocalDateTime.now());
    }
}
