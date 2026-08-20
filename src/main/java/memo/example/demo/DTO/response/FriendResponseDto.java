package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import memo.example.demo.domain.User;

/**
 * 수락된 친구 목록에 표시할 사용자 ID, 닉네임과 프로필 이미지를 반환한다.
 */
@Getter
@Builder
public class FriendResponseDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl;

    public static FriendResponseDto from(User user) {
        return FriendResponseDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}