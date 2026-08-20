package memo.example.demo.repository;

import memo.example.demo.domain.TeamTodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * TeamSpace 할 일 정보를 저장하고 팀 공간 또는 검색어 기준으로 할 일 목록을 조회한다.
 */
public interface TeamTodoRepository extends JpaRepository<TeamTodo, Long> {

    List<TeamTodo> findByTeamSpace_TeamSpaceId(Long teamSpaceId);

    // 할 일의 제목 또는 내용에 검색어가 포함된 항목을 찾는다.
    @Query("SELECT t FROM TeamTodo t WHERE t.title LIKE %:keyword% OR t.content LIKE %:keyword%")
    List<TeamTodo> searchByKeyword(@Param("keyword") String keyword);
}