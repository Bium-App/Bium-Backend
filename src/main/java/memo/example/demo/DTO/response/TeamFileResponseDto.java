package memo.example.demo.DTO.response;

import lombok.Builder;
import lombok.Getter;
import memo.example.demo.domain.TeamFile;
import java.time.LocalDateTime;

/**
 * TeamSpace 파일 목록에 업로더, 파일 위치와 메타데이터를 반환한다.
 */
@Getter
@Builder
public class TeamFileResponseDto {
    private Long fileId;
    private Long userId; // 업로드한 사용자
    private String fileName;
    // 실제 파일이 저장된 S3 위치다.
    private String fileUrl;
    private String fileSize;
    private LocalDateTime uploadedAt;

    public static TeamFileResponseDto from(TeamFile teamFile) {
        return TeamFileResponseDto.builder()
                .fileId(teamFile.getFileId())
                .userId(teamFile.getUser() != null ? teamFile.getUser().getUserId() : null)
                .fileName(teamFile.getFileName())
                .fileUrl(teamFile.getFileUrl())
                .fileSize(teamFile.getFileSize())
                .uploadedAt(teamFile.getUploadedAt())
                .build();
    }
}