package memo.example.demo.controller;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.FriendRequestDto;
import memo.example.demo.DTO.response.MessageResponseDto;
import memo.example.demo.config.jwt.LoginUser;
import memo.example.demo.service.FriendService;
import memo.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 친구 목록과 사용자 검색, 친구 요청 전송·조회·응답 API를 처리한다.
 */
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getFriends(
            @LoginUser Long userId,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "keyword", required = false) String keyword) {
        // SEARCH 또는 RECOMMEND는 사용자 검색을, 그 외에는 수락된 친구 목록 조회를 수행한다.
        if ("SEARCH".equalsIgnoreCase(type) || "RECOMMEND".equalsIgnoreCase(type)) {
            return ResponseEntity.ok(userService.searchUsersByKeyword(keyword));
        }
        return ResponseEntity.ok(friendService.getFriends(userId));
    }

    @PostMapping("/requests")
    public ResponseEntity<?> sendFriendRequest(
            @LoginUser Long userId,
            @RequestBody FriendRequestDto request) {
        Long requestId = friendService.sendFriendRequest(userId, request.getReceiverId());
        return ResponseEntity.ok(Map.of(
                "requestId", requestId,
                "message", "친구 요청이 전송되었습니다."
        ));
    }

    @GetMapping("/requests")
    public ResponseEntity<?> getRequests(@LoginUser Long userId, @RequestParam(name = "type", defaultValue = "RECEIVED") String type) {
        // SENT는 보낸 대기 요청을, 그 외에는 받은 대기 요청을 조회한다.
        if ("SENT".equalsIgnoreCase(type)) {
            return ResponseEntity.ok(friendService.getSentRequests(userId));
        }
        return ResponseEntity.ok(friendService.getPendingRequests(userId));
    }

    @PatchMapping("/requests/{requestId}")
    public ResponseEntity<MessageResponseDto> respondToRequest(
            @LoginUser Long userId,
            @PathVariable Long requestId,
            @RequestParam(name = "action") String action) {
        // ACCEPT는 친구 관계를 수락하고 REJECT는 거절 상태로 변경한다.
        if ("ACCEPT".equalsIgnoreCase(action)) {
            friendService.acceptRequest(userId, requestId);
        } else if ("REJECT".equalsIgnoreCase(action)) {
            friendService.rejectRequest(userId, requestId);
        } else {
            throw new IllegalArgumentException("지원하지 않는 친구 요청 액션입니다.");
        }
        return ResponseEntity.ok(new MessageResponseDto("처리 완료되었습니다."));
    }

    @DeleteMapping("/requests/{requestId}")
    public ResponseEntity<MessageResponseDto> cancelRequest(
            @LoginUser Long userId,
            @PathVariable Long requestId) {
        friendService.cancelRequest(userId, requestId);
        return ResponseEntity.ok(new MessageResponseDto("처리 완료되었습니다."));
    }
}
