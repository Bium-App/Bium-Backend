package memo.example.demo.controller;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.TeamNoticeRequestDto;
import memo.example.demo.DTO.response.MessageResponseDto;
import memo.example.demo.config.jwt.LoginUser;
import memo.example.demo.service.TeamNoticeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * TeamSpace 공지의 생성·조회·수정·삭제 API 요청을 처리한다.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeamNoticeController {

    private final TeamNoticeService teamNoticeService;

    @PostMapping("/team-spaces/{teamSpaceId}/notices")
    public ResponseEntity<MessageResponseDto> createNotice(
            @PathVariable Long teamSpaceId,
            @LoginUser Long userId,
            @RequestBody TeamNoticeRequestDto request) {
        teamNoticeService.createNotice(teamSpaceId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponseDto("팀 공지가 작성되었습니다."));
    }

    @GetMapping("/notices")
    public ResponseEntity<?> getNotices(@RequestParam(name = "teamSpaceId") Long teamSpaceId) {
        return ResponseEntity.ok(teamNoticeService.getNotices(teamSpaceId));
    }

    @GetMapping("/notices/{noticeId}")
    public ResponseEntity<?> getNoticeDetail(@PathVariable Long noticeId) {
        return ResponseEntity.ok(teamNoticeService.getNoticeDetail(noticeId));
    }

    @PatchMapping("/notices/{noticeId}")
    public ResponseEntity<MessageResponseDto> updateNotice(
            @PathVariable Long noticeId,
            @RequestBody TeamNoticeRequestDto request) {
        teamNoticeService.updateNotice(noticeId, request);
        return ResponseEntity.ok(new MessageResponseDto("공지가 수정되었습니다."));
    }

    @DeleteMapping("/notices/{noticeId}")
    public ResponseEntity<MessageResponseDto> deleteNotice(@PathVariable Long noticeId) {
        teamNoticeService.deleteNotice(noticeId);
        return ResponseEntity.ok(new MessageResponseDto("공지가 삭제되었습니다."));
    }
}