package memo.example.demo.service;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.MemoRequestDto;
import memo.example.demo.DTO.request.MemoUpdateRequestDto;
import memo.example.demo.DTO.response.MemoResponseDto;
import memo.example.demo.DTO.response.TrashResponseDto;
import memo.example.demo.domain.Memo;
import memo.example.demo.domain.Memo.MemoStatus;
import memo.example.demo.domain.MemoImage;
import memo.example.demo.domain.TeamSpace;
import memo.example.demo.domain.User;
import memo.example.demo.repository.MemoImageRepository;
import memo.example.demo.repository.MemoRepository;
import memo.example.demo.repository.TeamSpaceRepository;
import memo.example.demo.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 개인 및 팀 메모의 내용, FIRE/ICE 상태, 이미지와 TRASH 수명 주기를 관리한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MemoService {
    private final MemoRepository memoRepository;
    private final UserRepository userRepository;
    private final TeamSpaceRepository teamSpaceRepository;
    private final MemoImageRepository memoImageRepository;
    private final TeamAccessService teamAccessService;

    public Long createMemo(Long userId, MemoRequestDto request) {
        // 요청의 teamSpaceId로 연결할 TeamSpace를 조회해 메모의 저장 대상을 결정한다.
        User user = userRepository.findById(userId).orElseThrow();
        TeamSpace teamSpace = null;
        if (request.getTeamSpaceId() != null) {
            teamAccessService.requireMember(request.getTeamSpaceId(), userId);
            teamSpace = teamAccessService.requireActiveTeamSpace(request.getTeamSpaceId());
        }
        MemoStatus status = request.getStatus() != null
                ? MemoStatus.valueOf(request.getStatus().toUpperCase())
                : MemoStatus.ICE;

        Memo memo = Memo.builder()
                .user(user)
                .teamSpace(teamSpace)
                .status(status)
                .mTitle(request.getTitle())
                .mContent(request.getContent())
                .mRichContent(request.getRichContent())
                .expiredAt(status == MemoStatus.FIRE ? parseDateTimeSafe(request.getExpiredAt()) : null)
                .build();

        return memoRepository.save(memo).getMemoId();
    }

    @Transactional(readOnly = true)
    public List<MemoResponseDto> getUserMemos(Long userId) {
        // TeamSpace에 속하지 않고 TRASH로 이동하지 않은 개인 메모만 반환한다.
        return memoRepository.findByUser_UserIdAndTeamSpaceIsNull(userId).stream()
                .filter(m -> m.getDeletedAt() == null)
                .map(MemoResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MemoResponseDto> getTeamMemos(Long userId, Long teamSpaceId) {
        // 지정한 TeamSpace에서 TRASH로 이동하지 않은 메모만 반환한다.
        teamAccessService.requireMember(teamSpaceId, userId);
        return memoRepository.findByTeamSpace_TeamSpaceId(teamSpaceId).stream()
                .filter(m -> m.getDeletedAt() == null)
                .map(MemoResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MemoResponseDto getMemoDetail(Long userId, Long memoId) {
        Memo memo = requireAccessibleMemo(userId, memoId, false);
        // 메모 본문과 연결된 이미지 URL 정보를 하나의 상세 응답으로 구성한다.
        List<MemoImage> images = memoImageRepository.findByMemo_MemoId(memoId);
        return MemoResponseDto.from(memo, images);
    }

    @Transactional(readOnly = true)
    public List<TrashResponseDto> getTrashList(Long userId) {
        // 삭제 시각이 기록된 개인 메모를 현재 사용자의 TRASH 목록으로 반환한다.
        return memoRepository.findByUser_UserIdAndTeamSpaceIsNull(userId).stream()
                .filter(m -> m.getDeletedAt() != null)
                .map(TrashResponseDto::from)
                .collect(Collectors.toList());
    }

    public void updateMemo(Long userId, Long memoId, MemoUpdateRequestDto request) {
        Memo memo = requireAccessibleMemo(userId, memoId, false);
        // 요청에 포함된 값만 변경해 전달되지 않은 기존 내용은 유지한다.
        if(request.getTitle() != null) memo.setMTitle(request.getTitle());
        if(request.getContent() != null) memo.setMContent(request.getContent());
        if(request.getRichContent() != null) memo.setMRichContent(request.getRichContent());
    }

    public void updateStatus(Long userId, Long memoId, MemoStatus status) {
        Memo memo = requireAccessibleMemo(userId, memoId, false);
        memo.setStatus(status);
        if (status == MemoStatus.ICE) {
            memo.setExpiredAt(null);
        }
    }

    public void updatePin(Long userId, Long memoId, boolean isPinned) {
        Memo memo = requireAccessibleMemo(userId, memoId, false);
        memo.setIsPinned(isPinned);
    }

    public void moveMemoToTrash(Long userId, Long memoId) {
        Memo memo = requireAccessibleMemo(userId, memoId, false);
        // TRASH 이동 시 FIRE/ICE 상태는 유지하고 삭제 시각만 기록한다.
        memo.setDeletedAt(LocalDateTime.now());
    }

    public void restoreMemo(Long userId, Long memoId) {
        Memo memo = requirePersonalTrashMemo(userId, memoId);

        // 만료 시각이 있는 메모는 복구 시점부터 12시간 뒤로 만료를 연장한다.
        if (memo.getStatus() == MemoStatus.FIRE && memo.getExpiredAt() != null) {
            memo.setExpiredAt(LocalDateTime.now().plusHours(12));
        } else if (memo.getStatus() == MemoStatus.ICE) {
            memo.setExpiredAt(null);
        }

        memo.setDeletedAt(null); // 삭제 시각을 지워 TRASH에서 복구한다.
    }

    public void deleteMemosPermanently(Long userId, List<Long> memoIds) {
        if (memoIds == null || memoIds.isEmpty()) return;
        List<Memo> memos = memoIds.stream()
                .map(memoId -> requirePersonalTrashMemo(userId, memoId))
                .toList();
        deleteMemosPermanently(memos);
    }

    private void deleteMemosPermanently(List<Memo> memos) {
        // 메모를 삭제하기 전에 연결된 이미지 레코드를 먼저 제거한다.
        for (Memo memo : memos) {
            List<MemoImage> images = memoImageRepository.findByMemo_MemoId(memo.getMemoId());
            if (!images.isEmpty()) {
                memoImageRepository.deleteAll(images);
            }
        }
        memoRepository.deleteAll(memos);
    }

    private LocalDateTime parseDateTimeSafe(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) return null;
        String cleaned = dateTimeStr.length() >= 19 ? dateTimeStr.substring(0, 19) : dateTimeStr;
        return LocalDateTime.parse(cleaned);
    }

    @Scheduled(cron = "0 * * * * *")
    public void processExpiredMemos() {
        LocalDateTime now = LocalDateTime.now();

        // 만료 시각이 지났고 아직 TRASH에 없는 메모에 삭제 시각을 기록한다.
        int updatedCount = memoRepository.expireMemosToTrash(now, MemoStatus.FIRE);
        if (updatedCount > 0) {
            System.out.println("[Scheduler] " + updatedCount + "개의 불메모 휴지통 이동 처리 (" + now + ")");
        }

        LocalDateTime twentyFourHoursAgo = now.minusHours(24);
        // TRASH에서 24시간이 지난 메모와 연결 이미지를 영구 삭제한다.
        List<Memo> trashMemosToDelete = memoRepository.findByDeletedAtIsNotNullAndDeletedAtLessThanEqual(twentyFourHoursAgo);

        if (!trashMemosToDelete.isEmpty()) {
            List<Long> idsToDelete = trashMemosToDelete.stream().map(Memo::getMemoId).collect(Collectors.toList());
            deleteMemosPermanently(trashMemosToDelete);
            System.out.println("[Scheduler] 24시간 경과 휴지통 메모 " + idsToDelete.size() + "개 영구 삭제 완료");
        }
    }

    private Memo requireAccessibleMemo(Long userId, Long memoId, boolean allowDeleted) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("메모를 찾을 수 없습니다."));
        if (!allowDeleted && memo.getDeletedAt() != null) {
            throw new IllegalArgumentException("휴지통에 있는 메모입니다.");
        }
        if (memo.getTeamSpace() == null) {
            if (!memo.getUser().getUserId().equals(userId)) {
                throw new SecurityException("다른 사용자의 메모입니다.");
            }
        } else {
            teamAccessService.requireMember(memo.getTeamSpace().getTeamSpaceId(), userId);
        }
        return memo;
    }

    private Memo requirePersonalTrashMemo(Long userId, Long memoId) {
        Memo memo = requireAccessibleMemo(userId, memoId, true);
        if (memo.getTeamSpace() != null || !memo.getUser().getUserId().equals(userId)) {
            throw new SecurityException("개인 메모의 소유자만 처리할 수 있습니다.");
        }
        if (memo.getDeletedAt() == null) {
            throw new IllegalArgumentException("휴지통에 있는 메모가 아닙니다.");
        }
        return memo;
    }

}
