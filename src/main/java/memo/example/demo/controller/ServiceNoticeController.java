package memo.example.demo.controller;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.response.ServiceNoticeResponseDto;
import memo.example.demo.repository.ServiceNoticeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

/**
 * 서비스 전체 사용자에게 공개되는 운영 공지를 최신순으로 제공한다.
 */
@RestController
@RequestMapping("/api/service-notices")
@RequiredArgsConstructor
public class ServiceNoticeController {

    private final ServiceNoticeRepository serviceNoticeRepository;

    @GetMapping
    public ResponseEntity<?> getServiceNotices() {
        return ResponseEntity.ok(serviceNoticeRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(ServiceNoticeResponseDto::from)
                .collect(Collectors.toList()));
    }
}