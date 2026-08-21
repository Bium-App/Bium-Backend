package memo.example.demo.repository;

import memo.example.demo.domain.Friend;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 친구 관계 데이터를 저장하고 요청자·수신자·요청 상태를 기준으로 친구 관계를 조회한다.
 */
public interface FriendRepository extends JpaRepository<Friend, Long> {

    @EntityGraph(attributePaths = {"requester", "receiver"})
    List<Friend> findByRequester_UserId(Long requesterId);

    @EntityGraph(attributePaths = {"requester", "receiver"})
    List<Friend> findByReceiver_UserId(Long receiverId);

    @EntityGraph(attributePaths = {"requester", "receiver"})
    List<Friend> findByReceiver_UserIdAndStatus(Long receiverId, Friend.FriendStatus status);
}