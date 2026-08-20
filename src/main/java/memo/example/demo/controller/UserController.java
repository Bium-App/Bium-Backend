package memo.example.demo.controller;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.UserProfileUpdateRequestDto;
import memo.example.demo.DTO.request.UserSettingsUpdateRequestDto;
import memo.example.demo.DTO.response.MessageResponseDto;
import memo.example.demo.DTO.response.UserProfileResponseDto;
import memo.example.demo.config.jwt.LoginUser;
import memo.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 사용자 프로필과 설정 조회·변경, 회원 탈퇴 및 사용자 검색 API를 처리한다.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@LoginUser Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PatchMapping("/me")
    public ResponseEntity<MessageResponseDto> updateMyProfile(
            @LoginUser Long userId,
            @RequestBody UserProfileUpdateRequestDto request) {
        userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(new MessageResponseDto("프로필이 업데이트되었습니다."));
    }

    @GetMapping("/me/settings")
    public ResponseEntity<?> getMySettings(@LoginUser Long userId) {
        return ResponseEntity.ok(userService.getUserSettings(userId));
    }

    @PatchMapping("/me/settings")
    public ResponseEntity<MessageResponseDto> updateMySettings(
            @LoginUser Long userId,
            @RequestBody UserSettingsUpdateRequestDto request) {
        userService.updateUserSettings(userId, request);
        return ResponseEntity.ok(new MessageResponseDto("설정이 업데이트되었습니다."));
    }

    @DeleteMapping("/me")
    public ResponseEntity<MessageResponseDto> withdrawUser(@LoginUser Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(new MessageResponseDto("회원 탈퇴가 완료되었습니다."));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam(name = "nickname") String nickname) {
        List<UserProfileResponseDto> result = userService.searchUsersByKeyword(nickname);
        return ResponseEntity.ok(result != null && !result.isEmpty() ? result : Collections.emptyList());
    }
}