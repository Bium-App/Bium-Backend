package memo.example.demo.repository;

import memo.example.demo.domain.MemoImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 메모에 연결된 이미지 정보를 저장하고 메모 ID를 기준으로 첨부 이미지를 조회한다.
 */
public interface MemoImageRepository extends JpaRepository<MemoImage, Long> {
    List<MemoImage> findByMemo_MemoId(Long memoId);
}