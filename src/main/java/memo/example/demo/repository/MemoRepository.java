package memo.example.demo.repository;

import memo.example.demo.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 메모 데이터를 저장하고 개인·TeamSpace·검색·TRASH 및 만료 조건에 따라 메모를 조회하거나 처리한다.
 */
public interface MemoRepository extends JpaRepository<Memo, Long> {

    List<Memo> findByUser_UserIdAndTeamSpaceIsNull(Long userId);

    List<Memo> findByTeamSpace_TeamSpaceId(Long teamSpaceId);

    // 제목 또는 본문에 검색어가 포함된 메모를 찾는다.
    @Query("SELECT m FROM Memo m WHERE m.mTitle LIKE %:keyword% OR m.mContent LIKE %:keyword%")
    List<Memo> searchByKeyword(@Param("keyword") String keyword);

    // 만료 시각이 지났고 아직 TRASH로 이동하지 않은 메모에 현재 시각을 삭제 시각으로 기록한다.
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Memo m SET m.deletedAt = :now " +
            "WHERE m.expiredAt IS NOT NULL " +
            "AND m.status = :status " +
            "AND m.expiredAt <= :now " +
            "AND m.deletedAt IS NULL")
    int expireMemosToTrash(@Param("now") LocalDateTime now, @Param("status") Memo.MemoStatus status);

    // 삭제 시각을 기준으로 TRASH 보관 기한이 지난 메모를 찾는다.
    List<Memo> findByDeletedAtIsNotNullAndDeletedAtLessThanEqual(LocalDateTime dateTime);
}
