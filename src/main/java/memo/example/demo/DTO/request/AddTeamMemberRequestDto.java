package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TeamSpace에 추가할 사용자와 부여할 LEADER 또는 MEMBER 역할을 전달한다.
 */
@Getter
@NoArgsConstructor
public class AddTeamMemberRequestDto {
    private Long userId;
    private String role; // "LEADER", "MEMBER"
}