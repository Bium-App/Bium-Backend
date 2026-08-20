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

    public Long addImageToMemo(Long memoId, String imageUrl) {
        // 이미지 파일 자체가 아니라 업로드된 파일의 URL을 메모와 연결해 저장한다.
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("메모를 찾을 수 없습니다."));
        MemoImage memoImage = MemoImage.builder()
                .memo(memo)
                .imageUrl(imageUrl)
                .build();
        memoImage = memoImageRepository.save(memoImage);
        return memoImage.getImageId();
    }

    @Transactional(readOnly = true)
    public List<MemoImageResponseDto> getImagesByMemo(Long memoId) {
        return memoImageRepository.findByMemo_MemoId(memoId).stream()
                .map(img -> MemoImageResponseDto.builder()
                        .imageId(img.getImageId())
                        .imageUrl(img.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteImage(Long imageId) {
        memoImageRepository.deleteById(imageId);
    }
}