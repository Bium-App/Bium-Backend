package memo.example.demo.service;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.response.MemoImageResponseDto;
import memo.example.demo.domain.Memo;
import memo.example.demo.domain.MemoImage;
import memo.example.demo.repository.MemoImageRepository;
import memo.example.demo.repository.MemoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 메모와 연결된 이미지 URL을 등록·조회·삭제한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MemoImageService {
    private final MemoImageRepository memoImageRepository;
    private final MemoRepository memoRepository;
    private final TeamAccessService teamAccessService;

    public Long addImageToMemo(Long userId, Long memoId, String imageUrl) {
        // 이미지 파일 자체가 아니라 업로드된 파일의 URL을 메모와 연결해 저장한다.
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("메모를 찾을 수 없습니다."));
        requireMemoAccess(memo, userId);
        MemoImage memoImage = MemoImage.builder()
                .memo(memo)
                .imageUrl(imageUrl)
                .build();
        memoImage = memoImageRepository.save(memoImage);
        return memoImage.getImageId();
    }

    @Transactional(readOnly = true)
    public List<MemoImageResponseDto> getImagesByMemo(Long userId, Long memoId) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("메모를 찾을 수 없습니다."));
        requireMemoAccess(memo, userId);
        return memoImageRepository.findByMemo_MemoId(memoId).stream()
                .map(img -> MemoImageResponseDto.builder()
                        .imageId(img.getImageId())
                        .imageUrl(img.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteImage(Long userId, Long imageId) {
        MemoImage image = memoImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("메모 이미지를 찾을 수 없습니다."));
        requireMemoAccess(image.getMemo(), userId);
        memoImageRepository.delete(image);
    }

    private void requireMemoAccess(Memo memo, Long userId) {
        if (memo.getDeletedAt() != null) {
            throw new IllegalArgumentException("휴지통에 있는 메모입니다.");
        }
        if (memo.getTeamSpace() == null) {
            if (!memo.getUser().getUserId().equals(userId)) {
                throw new SecurityException("다른 사용자의 메모입니다.");
            }
        } else {
            teamAccessService.requireMember(memo.getTeamSpace().getTeamSpaceId(), userId);
        }
    }

}
