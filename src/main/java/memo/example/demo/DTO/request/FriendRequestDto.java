package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 친구 요청을 받을 사용자의 ID를 전달한다.
 */
@Getter
@NoArgsConstructor
public class FriendRequestDto {
    private Long receiverId;
}