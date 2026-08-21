package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * S3 업로드가 끝난 팀 파일의 이름, 접근 URL과 크기 정보를 전달한다.
 */
@Getter
@NoArgsConstructor
public class TeamFileRequestDto {
    private String fileName;
    // 실제 파일이 업로드된 S3 위치를 저장한다.
    private String fileUrl;
    private String fileSize;
}