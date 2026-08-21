package memo.example.demo.service;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.TeamNoticeRequestDto;
import memo.example.demo.DTO.response.TeamNoticeResponseDto;
import memo.example.demo.domain.Notification;
import memo.example.demo.domain.TeamMember;
import memo.example.demo.domain.TeamNotice;
import memo.example.demo.domain.TeamSpace;
import memo.example.demo.domain.User;
import memo.example.demo.repository.NotificationRepository;
import memo.example.demo.repository.TeamMemberRepository;
import memo.example.demo.repository.TeamNoticeRepository;
import memo.example.demo.repository.TeamSpaceRepository;
import memo.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 팀 공지를 관리하고 새 공지에 대한 팀원 알림을 생성한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TeamNoticeService {

    private final TeamNoticeRepository teamNoticeRepository;
    private final TeamSpaceRepository teamSpaceRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final NotificationRepository notificationRepository;
    private final TeamAccessService teamAccessService;

    public void createNotice(Long teamSpaceId, Long userId, TeamNoticeRequestDto request) {
        teamAccessService.requireMember(teamSpaceId, userId);
        TeamSpace teamSpace = teamAccessService.requireActiveTeamSpace(teamSpaceId);
        User user = userRepository.findById(userId).orElseThrow();

        TeamNotice notice = TeamNotice.builder()
                .teamSpace(teamSpace)
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .isPinned(request.getIsPinned() != null ? request.getIsPinned() : false)
                .build();
        teamNoticeRepository.save(notice);

        // 공지 작성자를 제외한 팀원에게 TEAM_NOTICE 알림을 생성한다.
        List<TeamMember> members = teamMemberRepository.findByTeamSpace_TeamSpaceId(teamSpaceId);
        for (TeamMember member : members) {
            if (!member.getUser().getUserId().equals(userId)) {
                Notification notification = Notification.builder()
                        .user(member.getUser())
                        .type(Notification.NotificationType.TEAM_NOTICE)
                        .targetId(notice.getNoticeId())
                        .message("팀스페이스에 새로운 공지사항이 등록되었습니다.")
                        .build();
                notificationRepository.save(notification);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<TeamNoticeResponseDto> getNotices(Long userId, Long teamSpaceId) {
        teamAccessService.requireMember(teamSpaceId, userId);
        return teamNoticeRepository.findByTeamSpace_TeamSpaceId(teamSpaceId).stream()
                .map(TeamNoticeResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeamNoticeResponseDto getNoticeDetail(Long userId, Long noticeId) {
        TeamNotice notice = teamNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
        teamAccessService.requireMember(notice.getTeamSpace().getTeamSpaceId(), userId);
        return TeamNoticeResponseDto.from(notice);
    }

    public void updateNotice(Long userId, Long noticeId, TeamNoticeRequestDto request) {
        TeamNotice notice = teamNoticeRepository.findById(noticeId).orElseThrow();
        teamAccessService.requireMember(notice.getTeamSpace().getTeamSpaceId(), userId);
        if (request.getTitle() != null) notice.setTitle(request.getTitle());
        if (request.getContent() != null) notice.setContent(request.getContent());
        if (request.getIsPinned() != null) notice.setIsPinned(request.getIsPinned());
    }

    public void deleteNotice(Long userId, Long noticeId) {
        TeamNotice notice = teamNoticeRepository.findById(noticeId).orElseThrow();
        teamAccessService.requireMember(notice.getTeamSpace().getTeamSpaceId(), userId);
        teamNoticeRepository.delete(notice);
    }
}
