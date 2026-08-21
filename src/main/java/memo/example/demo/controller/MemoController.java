package memo.example.demo.controller;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.DeleteTrashRequestDto;
import memo.example.demo.DTO.request.MemoRequestDto;
import memo.example.demo.DTO.request.MemoUpdateRequestDto;
import memo.example.demo.DTO.response.MessageResponseDto;
import memo.example.demo.config.jwt.LoginUser;
import memo.example.demo.domain.Memo.MemoStatus;
import memo.example.demo.service.MemoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 메모 생성·조회·수정과 상태 변경, TRASH 관련 API 요청을 처리한다.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemoController {
    private final MemoService memoService;

    @PostMapping("/memos")
    public ResponseEntity<?> createMemo(
            @LoginUser Long userId,
            @RequestBody MemoRequestDto request) {
        Long memoId = memoService.createMemo(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("memoId", memoId));
    }

    @GetMapping("/memos")
    public ResponseEntity<?> getMemos(
            @LoginUser Long userId,
            @RequestParam(name = "teamSpaceId", required = false) Long teamSpaceId) {
        // teamSpaceId가 있으면 팀 메모를, 없으면 현재 사용자의 개인 메모를 조회한다.
        if (teamSpaceId != null) {
            return ResponseEntity.ok(memoService.getTeamMemos(userId, teamSpaceId));
        }
        return ResponseEntity.ok(memoService.getUserMemos(userId));
    }

    @GetMapping("/memos/{memoId}")
    public ResponseEntity<?> getMemoDetail(@LoginUser Long userId, @PathVariable Long memoId) {
        return ResponseEntity.ok(memoService.getMemoDetail(userId, memoId));
    }

    @PatchMapping("/memos/{memoId}")
    public ResponseEntity<MessageResponseDto> updateMemo(
            @LoginUser Long userId,
            @PathVariable Long memoId,
            @RequestBody MemoUpdateRequestDto request) {
        memoService.updateMemo(userId, memoId, request);
        return ResponseEntity.ok(new MessageResponseDto("처리 완료되었습니다."));
    }

    @DeleteMapping("/memos/{memoId}")
    public ResponseEntity<MessageResponseDto> moveMemoToTrash(
            @LoginUser Long userId,
            @PathVariable Long memoId) {
        memoService.moveMemoToTrash(userId, memoId);
        return ResponseEntity.ok(new MessageResponseDto("휴지통으로 이동되었습니다."));
    }

    @PatchMapping("/memos/{memoId}/status")
    public ResponseEntity<MessageResponseDto> changeMemoStatus(
            @LoginUser Long userId,
            @PathVariable Long memoId,
            @RequestParam(name = "action") String action,
            @RequestParam(name = "value", required = false) String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("필수 파라미터가 누락되었습니다.");
        }
        // PIN은 상단 고정 여부를, STATUS는 FIRE 또는 ICE 상태를 변경한다.
        if ("PIN".equalsIgnoreCase(action)) {
            memoService.updatePin(userId, memoId, Boolean.parseBoolean(value));
        } else if ("STATUS".equalsIgnoreCase(action)) {
            try {
                memoService.updateStatus(userId, memoId, MemoStatus.valueOf(value.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("유효하지 않은 상태입니다.");
            }
        } else {
            throw new IllegalArgumentException("지원하지 않는 액션입니다.");
        }
        return ResponseEntity.ok(new MessageResponseDto("상태가 변경되었습니다."));
    }

    @GetMapping("/trash")
    public ResponseEntity<?> getTrashList(@LoginUser Long userId) {
        return ResponseEntity.ok(memoService.getTrashList(userId));
    }

    @PatchMapping("/trash/{memoId}/restore")
    public ResponseEntity<MessageResponseDto> restoreMemo(
            @LoginUser Long userId,
            @PathVariable Long memoId) {
        memoService.restoreMemo(userId, memoId);
        return ResponseEntity.ok(new MessageResponseDto("복구되었습니다."));
    }

    @DeleteMapping("/trash")
    public ResponseEntity<MessageResponseDto> deleteMemosPermanently(
            @LoginUser Long userId,
            @RequestBody DeleteTrashRequestDto request) {
        memoService.deleteMemosPermanently(userId, request.getMemoIds());
        return ResponseEntity.ok(new MessageResponseDto("영구 삭제 완료되었습니다."));
    }
}
