package memo.example.demo.controller;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.TeamSpaceCreateRequestDto;
import memo.example.demo.DTO.request.TeamSpaceRequestDto;
import memo.example.demo.DTO.response.MessageResponseDto;
import memo.example.demo.DTO.response.TeamSpaceResponseDto;
import memo.example.demo.config.jwt.LoginUser;
import memo.example.demo.service.TeamSpaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * TeamSpace 생성·조회·이름 변경·삭제 API 요청을 처리한다.
 */
@RestController
@RequestMapping("/api/team-spaces")
@RequiredArgsConstructor
public class TeamSpaceController {
    private final TeamSpaceService teamSpaceService;

    @PostMapping
    public ResponseEntity<?> createTeamSpace(@LoginUser Long userId, @RequestBody TeamSpaceCreateRequestDto request) {
        Long teamSpaceId = teamSpaceService.createTeamSpace(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("teamSpaceId", teamSpaceId));
    }

    @GetMapping
    public ResponseEntity<?> getMyTeamSpaces(@LoginUser Long userId) {
        List<TeamSpaceResponseDto> spaces = teamSpaceService.getMyTeamSpaces(userId);
        return ResponseEntity.ok(spaces != null && !spaces.isEmpty() ? spaces : Collections.emptyList());
    }

    @GetMapping("/{teamSpaceId}")
    public ResponseEntity<?> getTeamSpaceDetail(@LoginUser Long userId, @PathVariable Long teamSpaceId) {
        return ResponseEntity.ok(teamSpaceService.getTeamSpace(userId, teamSpaceId));
    }

    @PatchMapping("/{teamSpaceId}")
    public ResponseEntity<MessageResponseDto> updateTeamSpace(
            @LoginUser Long userId,
            @PathVariable Long teamSpaceId,
            @RequestBody TeamSpaceRequestDto request) {
        teamSpaceService.updateTeamSpaceName(userId, teamSpaceId, request);
        return ResponseEntity.ok(new MessageResponseDto("팀 스페이스 이름이 수정되었습니다."));
    }

    @DeleteMapping("/{teamSpaceId}")
    public ResponseEntity<MessageResponseDto> deleteTeamSpace(
            @LoginUser Long userId,
            @PathVariable Long teamSpaceId) {
        teamSpaceService.deleteTeamSpace(userId, teamSpaceId);
        return ResponseEntity.ok(new MessageResponseDto("팀 스페이스가 삭제되었습니다."));
    }
}
