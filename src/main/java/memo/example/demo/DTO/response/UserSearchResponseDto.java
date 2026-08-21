package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import memo.example.demo.domain.User;

/**
 * 친구 추가와 TeamSpace 초대 검색에 필요한 공개 프로필 정보만 반환한다.
 */
@Getter
@Builder
public class UserSearchResponseDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl;

    public static UserSearchResponseDto from(User user) {
        return UserSearchResponseDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
