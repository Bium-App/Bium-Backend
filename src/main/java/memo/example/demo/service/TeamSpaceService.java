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
 * 팀 스페이스를 생성·조회하고 이름 변경과 soft delete를 관리한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TeamSpaceService {
    private final TeamSpaceRepository teamSpaceRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    public Long createTeamSpace(Long userId, TeamSpaceCreateRequestDto request) {
        TeamSpace teamSpace = TeamSpace.builder()
                .name(request.getName())
                .build();
        teamSpace = teamSpaceRepository.save(teamSpace);
        User user = userRepository.findById(userId).orElseThrow();
        // 팀 스페이스 생성자는 최초 LEADER 팀원으로 함께 등록한다.
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
                .map(tm -> TeamSpaceResponseDto.from(tm.getTeamSpace(), teamMemberRepository.findByTeamSpace_TeamSpaceId(tm.getTeamSpace().getTeamSpaceId()).size()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeamSpaceResponseDto getTeamSpace(Long teamSpaceId) {
        TeamSpace teamSpace = teamSpaceRepository.findById(teamSpaceId)
                .orElseThrow(() -> new IllegalArgumentException("팀 스페이스를 찾을 수 없습니다."));
        Integer memberCount = teamMemberRepository.findByTeamSpace_TeamSpaceId(teamSpaceId).size();
        return TeamSpaceResponseDto.from(teamSpace, memberCount);
    }

    public void updateTeamSpaceName(Long teamSpaceId, TeamSpaceRequestDto request) {
        TeamSpace teamSpace = teamSpaceRepository.findById(teamSpaceId)
                .orElseThrow(() -> new IllegalArgumentException("팀 스페이스를 찾을 수 없습니다."));
        if (request.getName() != null && !request.getName().isBlank()) {
            teamSpace.setName(request.getName());
        }
    }

    public void deleteTeamSpace(Long teamSpaceId) {
        TeamSpace teamSpace = teamSpaceRepository.findById(teamSpaceId)
                .orElseThrow(() -> new IllegalArgumentException("팀 스페이스를 찾을 수 없습니다."));
        // 실제 레코드는 유지하고 삭제 시각을 기록해 soft delete 처리한다.
        teamSpace.setDeletedAt(LocalDateTime.now());
    }
}