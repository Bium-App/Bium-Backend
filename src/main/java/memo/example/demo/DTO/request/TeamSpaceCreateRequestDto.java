package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 새로운 TeamSpace를 만들 때 사용할 이름을 전달한다.
 */
@Getter
@NoArgsConstructor
public class TeamSpaceCreateRequestDto {
    private String name;
}