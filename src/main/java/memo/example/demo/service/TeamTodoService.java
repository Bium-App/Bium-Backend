package memo.example.demo.service;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.TeamTodoRequestDto;
import memo.example.demo.DTO.request.TeamTodoUpdateRequestDto;
import memo.example.demo.DTO.response.TeamTodoResponseDto;
import memo.example.demo.domain.TeamSpace;
import memo.example.demo.domain.TeamTodo;
import memo.example.demo.domain.User;
import memo.example.demo.repository.TeamSpaceRepository;
import memo.example.demo.repository.TeamTodoRepository;
import memo.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 팀 스페이스의 할 일과 완료 여부, 마감일 및 알림 설정을 관리한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TeamTodoService {
    private final TeamTodoRepository teamTodoRepository;
    private final TeamSpaceRepository teamSpaceRepository;
    private final UserRepository userRepository;
    private final TeamAccessService teamAccessService;

    public void createTodo(Long teamSpaceId, Long userId, TeamTodoRequestDto request) {
        // 새 할 일은 미완료 상태로 만들고 sendPush는 설정값만 저장한다.
        // 이 Service에서는 실제 푸시 알림을 발송하지 않는다.
        teamAccessService.requireMember(teamSpaceId, userId);
        TeamSpace teamSpace = teamAccessService.requireActiveTeamSpace(teamSpaceId);
        User user = userRepository.findById(userId).orElseThrow();

        TeamTodo todo = TeamTodo.builder()
                .teamSpace(teamSpace)
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .dueDate(request.getDueDate() != null ? LocalDate.parse(request.getDueDate()) : null)
                .sendPush(request.getSendPush() != null ? request.getSendPush() : false)
                .isChecked(false)
                .build();
        teamTodoRepository.save(todo);
    }

    @Transactional(readOnly = true)
    public List<TeamTodoResponseDto> getTodosByTeamSpace(Long userId, Long teamSpaceId) {
        teamAccessService.requireMember(teamSpaceId, userId);
        return teamTodoRepository.findByTeamSpace_TeamSpaceId(teamSpaceId).stream()
                .map(TeamTodoResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeamTodoResponseDto getTodoDetail(Long userId, Long todoId) {
        TeamTodo t = teamTodoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("할 일을 찾을 수 없습니다."));
        teamAccessService.requireMember(t.getTeamSpace().getTeamSpaceId(), userId);
        return TeamTodoResponseDto.from(t);
    }

    public void updateTodo(Long userId, Long todoId, TeamTodoUpdateRequestDto request) {
        TeamTodo todo = teamTodoRepository.findById(todoId).orElseThrow();
        teamAccessService.requireMember(todo.getTeamSpace().getTeamSpaceId(), userId);
        // 요청에 포함된 값만 변경해 기존 할 일의 나머지 정보는 유지한다.
        if (request.getTitle() != null) todo.setTitle(request.getTitle());
        if (request.getContent() != null) todo.setContent(request.getContent());
        if (request.getIsChecked() != null) todo.setIsChecked(request.getIsChecked());
        if (request.getDueDate() != null) todo.setDueDate(LocalDate.parse(request.getDueDate()));
        if (request.getSendPush() != null) todo.setSendPush(request.getSendPush());
    }

    public void deleteTodo(Long userId, Long todoId) {
        TeamTodo todo = teamTodoRepository.findById(todoId).orElseThrow();
        teamAccessService.requireMember(todo.getTeamSpace().getTeamSpaceId(), userId);
        teamTodoRepository.delete(todo);
    }
}
