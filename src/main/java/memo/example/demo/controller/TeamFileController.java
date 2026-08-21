package memo.example.demo.controller;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.FileRenameRequestDto;
import memo.example.demo.DTO.request.TeamFileRequestDto;
import memo.example.demo.DTO.response.MessageResponseDto;
import memo.example.demo.config.jwt.LoginUser;
import memo.example.demo.service.TeamFileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * TeamSpace 파일 메타데이터의 등록·조회·이름 변경·삭제 API를 처리한다.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeamFileController {

    private final TeamFileService teamFileService;

    @PostMapping("/team-spaces/{teamSpaceId}/files")
    public ResponseEntity<MessageResponseDto> uploadFileInfo(
            @PathVariable Long teamSpaceId,
            @LoginUser Long userId,
            @RequestBody TeamFileRequestDto request) {
        teamFileService.saveFileInfo(teamSpaceId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponseDto("파일 정보가 저장되었습니다."));
    }

    @GetMapping("/team-spaces/{teamSpaceId}/files")
    public ResponseEntity<?> getTeamFiles(@LoginUser Long userId, @PathVariable Long teamSpaceId) {
        return ResponseEntity.ok(teamFileService.getTeamFiles(userId, teamSpaceId));
    }

    @PatchMapping("/team-files/{fileId}")
    public ResponseEntity<MessageResponseDto> renameTeamFile(
            @LoginUser Long userId,
            @PathVariable Long fileId,
            @RequestBody FileRenameRequestDto request) {
        teamFileService.renameFile(userId, fileId, request.getNewFileName());
        return ResponseEntity.ok(new MessageResponseDto("파일 이름이 변경되었습니다."));
    }

    @DeleteMapping("/team-files/{fileId}")
    public ResponseEntity<MessageResponseDto> deleteFile(
            @LoginUser Long userId,
            @PathVariable Long fileId) {
        teamFileService.deleteFile(userId, fileId);
        return ResponseEntity.ok(new MessageResponseDto("파일이 삭제되었습니다."));
    }
}
