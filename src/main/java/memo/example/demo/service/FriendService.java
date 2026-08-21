package memo.example.demo.service;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.response.FriendRequestResponseDto;
import memo.example.demo.DTO.response.FriendResponseDto;
import memo.example.demo.domain.Friend;
import memo.example.demo.domain.Friend.FriendStatus;
import memo.example.demo.domain.Notification;
import memo.example.demo.domain.User;
import memo.example.demo.repository.FriendRepository;
import memo.example.demo.repository.NotificationRepository;
import memo.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 친구 요청의 중복을 막고 요청 상태, 친구 목록과 관련 알림을 관리한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public Long sendFriendRequest(Long requesterId, Long receiverId) {
        if (requesterId.equals(receiverId)) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        // 요청 방향과 관계없이 이미 대기 중이거나 수락된 관계가 있으면 중복 요청을 막는다.
        List<Friend> requesters = friendRepository.findByRequester_UserId(requesterId);
        boolean alreadySent = requesters.stream().anyMatch(f -> f.getReceiver().getUserId().equals(receiverId) && (f.getStatus() == FriendStatus.PENDING || f.getStatus() == FriendStatus.ACCEPTED));
        if (alreadySent) {
            throw new IllegalStateException("이미 대기 중이거나 수락된 친구 요청이 존재합니다.");
        }

        List<Friend> receivers = friendRepository.findByReceiver_UserId(requesterId);
        boolean alreadyReceived = receivers.stream().anyMatch(f -> f.getRequester().getUserId().equals(receiverId) && (f.getStatus() == FriendStatus.PENDING || f.getStatus() == FriendStatus.ACCEPTED));
        if (alreadyReceived) {
            throw new IllegalStateException("이미 대기 중이거나 수락된 친구 요청이 존재합니다.");
        }

        User requester = userRepository.findById(requesterId).orElseThrow();
        User receiver = userRepository.findById(receiverId).orElseThrow();

        Friend friend = Friend.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendStatus.PENDING)
                .build();
        friend = friendRepository.save(friend);

        // 친구 요청 알림의 targetId에는 생성된 요청 ID를 저장한다.
        Notification notification = Notification.builder()
                .user(receiver)
                .type(Notification.NotificationType.FRIEND_REQUEST)
                .targetId(friend.getRequestId())
                .message("친구 요청이 도착했습니다.")
                .build();
        notificationRepository.save(notification);

        return friend.getRequestId();
    }

    @Transactional(readOnly = true)
    public List<FriendRequestResponseDto> getPendingRequests(Long userId) {
        return friendRepository.findByReceiver_UserIdAndStatus(userId, FriendStatus.PENDING).stream()
                .map(f -> FriendRequestResponseDto.from(f, f.getRequester().getNickname()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FriendRequestResponseDto> getSentRequests(Long userId) {
        return friendRepository.findByRequester_UserId(userId).stream()
                .filter(f -> f.getStatus() == FriendStatus.PENDING)
                .map(f -> FriendRequestResponseDto.from(f, f.getReceiver().getNickname()))
                .collect(Collectors.toList());
    }

    public void acceptRequest(Long userId, Long requestId) {
        Friend friend = requirePendingReceiverRequest(userId, requestId);
        friend.setStatus(FriendStatus.ACCEPTED);
    }

    public void rejectRequest(Long userId, Long requestId) {
        Friend friend = requirePendingReceiverRequest(userId, requestId);
        friend.setStatus(FriendStatus.REJECTED);
    }

    public void cancelRequest(Long userId, Long requestId) {
        Friend friend = friendRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청을 찾을 수 없습니다."));
        if (!friend.getRequester().getUserId().equals(userId)) {
            throw new SecurityException("친구 요청을 보낸 사용자만 취소할 수 있습니다.");
        }
        if (friend.getStatus() != FriendStatus.PENDING) {
            throw new IllegalStateException("대기 중인 친구 요청만 취소할 수 있습니다.");
        }
        friendRepository.delete(friend);
    }

    @Transactional(readOnly = true)
    public List<FriendResponseDto> getFriends(Long userId) {
        List<Friend> requesters = friendRepository.findByRequester_UserId(userId);
        List<Friend> receivers = friendRepository.findByReceiver_UserId(userId);

        List<FriendResponseDto> friends = new ArrayList<>();

        // 요청을 보낸 경우와 받은 경우 모두 ACCEPTED 관계의 상대방만 친구로 반환한다.
        requesters.stream()
                .filter(f -> f.getStatus() == FriendStatus.ACCEPTED)
                .forEach(f -> friends.add(FriendResponseDto.from(f.getReceiver())));

        receivers.stream()
                .filter(f -> f.getStatus() == FriendStatus.ACCEPTED)
                .forEach(f -> friends.add(FriendResponseDto.from(f.getRequester())));

        return friends;
    }

    private Friend requirePendingReceiverRequest(Long userId, Long requestId) {
        Friend friend = friendRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청을 찾을 수 없습니다."));
        if (!friend.getReceiver().getUserId().equals(userId)) {
            throw new SecurityException("친구 요청을 받은 사용자만 처리할 수 있습니다.");
        }
        if (friend.getStatus() != FriendStatus.PENDING) {
            throw new IllegalStateException("대기 중인 친구 요청만 처리할 수 있습니다.");
        }
        return friend;
    }
}
