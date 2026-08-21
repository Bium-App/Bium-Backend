package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기존 TeamSpace의 이름을 변경할 때 새 이름을 전달한다.
 */
@Getter
@NoArgsConstructor
public class TeamSpaceRequestDto {
    private String name;
}