package memo.example.demo.controller;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.AddTeamMemberRequestDto;
import memo.example.demo.DTO.response.MessageResponseDto;
import memo.example.demo.domain.TeamMember.Role;
import memo.example.demo.service.TeamMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * TeamSpace 구성원 추가·조회·역할 변경·제거 API 요청을 처리한다.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeamMemberController {
    private final TeamMemberService teamMemberService;

    @PostMapping("/team-spaces/{teamSpaceId}/members")
    public ResponseEntity<MessageResponseDto> addTeamMember(
            @PathVariable Long teamSpaceId,
            @RequestBody AddTeamMemberRequestDto request) {
        teamMemberService.addMember(teamSpaceId, request.getUserId(), Role.valueOf(request.getRole().toUpperCase()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponseDto("팀원이 추가되었습니다."));
    }

    @GetMapping("/team-members/team/{teamSpaceId}")
    public ResponseEntity<?> getTeamMembers(@PathVariable Long teamSpaceId) {
        return ResponseEntity.ok(teamMemberService.getTeamMembers(teamSpaceId));
    }

    @PatchMapping("/team-members/{memberId}")
    public ResponseEntity<MessageResponseDto> changeMemberRole(
            @PathVariable Long memberId,
            @RequestParam(name = "role") String role) {
        teamMemberService.changeRole(memberId, Role.valueOf(role.toUpperCase()));
        return ResponseEntity.ok(new MessageResponseDto("역할이 변경되었습니다."));
    }

    @DeleteMapping("/team-members/{memberId}")
    public ResponseEntity<MessageResponseDto> removeMember(@PathVariable Long memberId) {
        teamMemberService.removeMember(memberId);
        return ResponseEntity.ok(new MessageResponseDto("팀원이 삭제되었습니다."));
    }
}