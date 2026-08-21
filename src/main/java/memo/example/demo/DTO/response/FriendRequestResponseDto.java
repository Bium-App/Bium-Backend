package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import memo.example.demo.domain.Friend;
import java.time.LocalDateTime;

/**
 * 보낸 요청 또는 받은 요청 목록에 친구 요청 ID와 상대방 닉네임을 반환한다.
 */
@Getter
@Builder
public class FriendRequestResponseDto {
    private Long requestId;
    private String nickname;
    private LocalDateTime createdAt;

    public static FriendRequestResponseDto from(Friend friend, String targetNickname) {
        return FriendRequestResponseDto.builder()
                .requestId(friend.getRequestId())
                .nickname(targetNickname)
                .createdAt(friend.getCreatedAt())
                .build();
    }
}