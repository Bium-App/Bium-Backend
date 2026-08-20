package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 프로필에서 변경할 이름, 연락처와 프로필 이미지 URL을 전달한다.
 */
@Getter
@NoArgsConstructor
public class UserProfileUpdateRequestDto {
    private String nickname;
    private String profileImageUrl;
    private String name;
    private String email;
    private String phoneNumber;
}