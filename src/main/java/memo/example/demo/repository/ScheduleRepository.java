package memo.example.demo.repository;

import memo.example.demo.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 개인 및 TeamSpace 일정 정보를 저장하고 사용자·팀·검색어·연월 조건으로 일정을 조회한다.
 */
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByTeamSpace_TeamSpaceId(Long teamSpaceId);

    List<Schedule> findByUser_UserIdAndTeamSpaceIsNull(Long userId);

    // 일정 제목 또는 내용에 검색어가 포함된 일정을 찾는다.
    @Query("SELECT s FROM Schedule s WHERE s.sTitle LIKE %:keyword% OR s.sContent LIKE %:keyword%")
    List<Schedule> searchByKeyword(@Param("keyword") String keyword);

    // 시작 시각의 연도와 월을 기준으로 TeamSpace 일정을 조회한다.
    @Query("SELECT s FROM Schedule s WHERE s.teamSpace.teamSpaceId = :teamSpaceId " +
            "AND YEAR(s.startAt) = :year AND MONTH(s.startAt) = :month")
    List<Schedule> findByTeamSpaceAndMonth(
            @Param("teamSpaceId") Long teamSpaceId,
            @Param("year") int year,
            @Param("month") int month);

    // TeamSpace에 속하지 않은 개인 일정만 시작 시각의 연도와 월로 조회한다.
    @Query("SELECT s FROM Schedule s WHERE s.user.userId = :userId AND s.teamSpace IS NULL " +
            "AND YEAR(s.startAt) = :year AND MONTH(s.startAt) = :month")
    List<Schedule> findByUserAndMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month);
}