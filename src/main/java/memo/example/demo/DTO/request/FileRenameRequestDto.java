package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 팀 파일의 표시 이름을 변경할 때 새 이름을 전달한다.
 */
@Getter
@NoArgsConstructor
public class FileRenameRequestDto {
    private String newFileName;
}