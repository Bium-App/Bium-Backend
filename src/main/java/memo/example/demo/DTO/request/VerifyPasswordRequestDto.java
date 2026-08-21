package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 확인 API에 사용자가 입력한 비밀번호를 전달한다.
 */
@Getter
@NoArgsConstructor
public class VerifyPasswordRequestDto {
    private String password;
}