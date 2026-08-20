package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import memo.example.demo.domain.User;

/**
 * 사용자 프로필 화면에 표시할 이름, 연락처와 프로필 이미지를 반환한다.
 */
@Getter
@Builder
public class UserProfileResponseDto {
    private Long userId;
    private String name;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;

    public static UserProfileResponseDto from(User user) {
        return UserProfileResponseDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}