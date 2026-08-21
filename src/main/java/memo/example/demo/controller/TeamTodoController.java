package memo.example.demo.controller;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.TeamTodoRequestDto;
import memo.example.demo.DTO.request.TeamTodoUpdateRequestDto;
import memo.example.demo.DTO.response.MessageResponseDto;
import memo.example.demo.config.jwt.LoginUser;
import memo.example.demo.service.TeamTodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * TeamSpace 할 일의 생성·조회·수정·삭제 API 요청을 처리한다.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeamTodoController {
    private final TeamTodoService teamTodoService;

    @PostMapping("/team-spaces/{teamSpaceId}/todos")
    public ResponseEntity<MessageResponseDto> createTodo(
            @PathVariable Long teamSpaceId,
            @LoginUser Long userId,
            @RequestBody TeamTodoRequestDto request) {
        teamTodoService.createTodo(teamSpaceId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponseDto("할 일 생성 완료"));
    }

    @GetMapping("/todos")
    public ResponseEntity<?> getTodos(
            @LoginUser Long userId,
            @RequestParam(name = "teamSpaceId") Long teamSpaceId) {
        return ResponseEntity.ok(teamTodoService.getTodosByTeamSpace(userId, teamSpaceId));
    }

    @GetMapping("/todos/{todoId}")
    public ResponseEntity<?> getTodoDetail(@LoginUser Long userId, @PathVariable Long todoId) {
        return ResponseEntity.ok(teamTodoService.getTodoDetail(userId, todoId));
    }

    @PatchMapping("/todos/{todoId}")
    public ResponseEntity<MessageResponseDto> updateTodo(
            @LoginUser Long userId,
            @PathVariable Long todoId,
            @RequestBody TeamTodoUpdateRequestDto request) {
        teamTodoService.updateTodo(userId, todoId, request);
        return ResponseEntity.ok(new MessageResponseDto("할 일 업데이트 완료"));
    }

    @DeleteMapping("/todos/{todoId}")
    public ResponseEntity<MessageResponseDto> deleteTodo(
            @LoginUser Long userId,
            @PathVariable Long todoId) {
        teamTodoService.deleteTodo(userId, todoId);
        return ResponseEntity.ok(new MessageResponseDto("할 일 삭제 완료"));
    }
}
