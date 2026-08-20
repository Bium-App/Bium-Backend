package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import memo.example.demo.domain.TeamSpace;
import java.time.LocalDateTime;

/**
 * TeamSpace의 이름, 참여 인원 수와 생성 시각을 반환한다.
 */
@Getter
@Builder
public class TeamSpaceResponseDto {
    private Long teamSpaceId;
    private String name;
    private Integer memberCount;
    private LocalDateTime createdAt;

    public static TeamSpaceResponseDto from(TeamSpace teamSpace, Integer memberCount) {
        return TeamSpaceResponseDto.builder()
                .teamSpaceId(teamSpace.getTeamSpaceId())
                .name(teamSpace.getName())
                .memberCount(memberCount)
                .createdAt(teamSpace.getCreatedAt())
                .build();
    }
}