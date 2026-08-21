package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 클라이언트가 S3에 직접 파일을 올릴 URL과 업로드 후 파일 위치를 반환한다.
 */
@Getter
@Builder
public class PresignedUrlResponseDto {
    // 제한된 시간 동안 S3 업로드에 사용하는 주소다.
    private String presignedUrl;
    // 업로드된 파일을 서비스에서 참조할 위치다.
    private String fileUrl;
}