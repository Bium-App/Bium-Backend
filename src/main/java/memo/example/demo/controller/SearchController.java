package memo.example.demo.controller;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.response.SearchResponseDto;
import memo.example.demo.config.jwt.LoginUser;
import memo.example.demo.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 메모, 팀 할 일과 일정의 통합 검색 API 요청을 처리한다.
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<SearchResponseDto> globalSearch(
            @LoginUser Long userId,
            @RequestParam(name = "keyword") String keyword) {
        return ResponseEntity.ok(searchService.globalSearch(userId, keyword));
    }
}
