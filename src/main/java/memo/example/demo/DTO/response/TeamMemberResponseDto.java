package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import memo.example.demo.domain.TeamMember;

/**
 * TeamSpace 구성원 목록에 사용자 정보와 LEADER 또는 MEMBER 역할을 반환한다.
 */
@Getter
@Builder
public class TeamMemberResponseDto {
    private Long teamMemberId;
    private Long userId;
    private String nickname;
    private String role;

    public static TeamMemberResponseDto from(TeamMember teamMember) {
        return TeamMemberResponseDto.builder()
                .teamMemberId(teamMember.getTeamMemberId())
                .userId(teamMember.getUser() != null ? teamMember.getUser().getUserId() : null)
                .nickname(teamMember.getUser() != null ? teamMember.getUser().getNickname() : null)
                .role(teamMember.getRole() != null ? teamMember.getRole().name() : null)
                .build();
    }
}