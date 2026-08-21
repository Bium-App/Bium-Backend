package memo.example.demo.controller;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.ScheduleRequestDto;
import memo.example.demo.DTO.response.MessageResponseDto;
import memo.example.demo.config.jwt.LoginUser;
import memo.example.demo.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 개인 및 TeamSpace 일정의 생성·월별 조회·수정·삭제 API 요청을 처리한다.
 */
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<MessageResponseDto> createSchedule(
            @LoginUser Long userId,
            @RequestBody ScheduleRequestDto request) {
        scheduleService.createSchedule(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponseDto("일정 생성 완료"));
    }

    @GetMapping
    public ResponseEntity<?> getSchedules(
            @LoginUser Long userId,
            @RequestParam(name = "year") int year,
            @RequestParam(name = "month") int month,
            @RequestParam(name = "teamSpaceId", required = false) Long teamSpaceId) {

        // teamSpaceId가 있으면 팀 일정을, 없으면 현재 사용자의 개인 일정을 월별로 조회한다.
        if (teamSpaceId != null) {
            return ResponseEntity.ok(scheduleService.getTeamSchedulesByMonth(userId, teamSpaceId, year, month));
        }
        return ResponseEntity.ok(scheduleService.getUserSchedulesByMonth(userId, year, month));
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getScheduleDetail(@LoginUser Long userId, @PathVariable Long scheduleId) {
        return ResponseEntity.ok(scheduleService.getScheduleDetail(userId, scheduleId));
    }

    @PatchMapping("/{scheduleId}")
    public ResponseEntity<MessageResponseDto> updateSchedule(
            @LoginUser Long userId,
            @PathVariable Long scheduleId,
            @RequestBody ScheduleRequestDto request) {
        scheduleService.updateSchedule(userId, scheduleId, request);
        return ResponseEntity.ok(new MessageResponseDto("일정 수정 완료"));
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<MessageResponseDto> deleteSchedule(
            @LoginUser Long userId,
            @PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(userId, scheduleId);
        return ResponseEntity.ok(new MessageResponseDto("일정 삭제 완료"));
    }
}
