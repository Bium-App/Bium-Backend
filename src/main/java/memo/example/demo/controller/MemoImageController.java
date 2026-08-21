package memo.example.demo.controller;

import lombok.RequiredArgsConstructor;
import memo.example.demo.config.jwt.LoginUser;
import memo.example.demo.service.MemoImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 메모와 이미지 URL 정보의 연결·조회·삭제 API 요청을 처리한다.
 */
@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class MemoImageController {
    private final MemoImageService memoImageService;

    @PostMapping("/{memoId}/images")
    public ResponseEntity<?> addMemoImage(
            @LoginUser Long userId,
            @PathVariable Long memoId,
            @RequestBody Map<String, String> request) {
        Long realImageId = memoImageService.addImageToMemo(userId, memoId, request.get("imageUrl"));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("imageId", realImageId));
    }

    @GetMapping("/{memoId}/images")
    public ResponseEntity<?> getMemoImages(@LoginUser Long userId, @PathVariable Long memoId) {
        return ResponseEntity.ok(memoImageService.getImagesByMemo(userId, memoId));
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteMemoImage(@LoginUser Long userId, @PathVariable Long imageId) {
        memoImageService.deleteImage(userId, imageId);
        return ResponseEntity.noContent().build();
    }
}
