package memo.example.demo.service;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.InquiryRequestDto;
import memo.example.demo.DTO.response.InquiryResponseDto;
import memo.example.demo.domain.Inquiry;
import memo.example.demo.domain.Inquiry.InquiryType;
import memo.example.demo.domain.User;
import memo.example.demo.repository.InquiryRepository;
import memo.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 문의를 접수하고 답변과 WAITING/ANSWERED 처리 상태를 관리한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    public void createInquiry(Long userId, InquiryRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .type(InquiryType.valueOf(request.getType()))
                .title(request.getTitle())
                .content(request.getContent())
                .attachmentUrl(request.getAttachmentUrl())
                .build();
        inquiryRepository.save(inquiry);
    }

    @Transactional(readOnly = true)
    public List<InquiryResponseDto> getUserInquiries(Long userId) {
        return inquiryRepository.findByUser_UserId(userId).stream()
                .map(InquiryResponseDto::from)
                .collect(Collectors.toList());
    }

    public void addResponse(Long inquiryId, String responseContent) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다."));
        // 답변을 저장한 문의는 ANSWERED 상태로 전환한다.
        inquiry.setResponse(responseContent);
        inquiry.setStatus(Inquiry.InquiryStatus.ANSWERED);
    }
}