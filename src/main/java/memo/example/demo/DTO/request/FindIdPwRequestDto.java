package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이메일로 로그인 아이디를 찾을 때 요청 유형과 이메일을 전달한다.
 */
@Getter
@NoArgsConstructor
public class FindIdPwRequestDto {
    private String type; // 아이디 찾기 요청은 ID 값을 사용한다.
    private String email;
}