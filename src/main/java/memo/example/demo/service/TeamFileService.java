package memo.example.demo.service;

import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.request.TeamFileRequestDto;
import memo.example.demo.DTO.response.TeamFileResponseDto;
import memo.example.demo.domain.TeamFile;
import memo.example.demo.domain.TeamSpace;
import memo.example.demo.domain.User;
import memo.example.demo.repository.TeamFileRepository;
import memo.example.demo.repository.TeamSpaceRepository;
import memo.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 팀 스페이스에 업로드된 파일의 메타데이터와 이름을 관리한다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TeamFileService {
    private final TeamFileRepository teamFileRepository;
    private final TeamSpaceRepository teamSpaceRepository;
    private final UserRepository userRepository;

    public void saveFileInfo(Long teamSpaceId, Long userId, TeamFileRequestDto request) {
        // 실제 파일은 S3에 두고 이 Service는 파일 URL과 이름, 크기 및 업로더를 저장한다.
        TeamSpace teamSpace = teamSpaceRepository.findById(teamSpaceId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        TeamFile teamFile = TeamFile.builder()
                .teamSpace(teamSpace)
                .user(user)
                .fileName(request.getFileName())
                .fileUrl(request.getFileUrl())
                .fileSize(request.getFileSize())
                .build();
        teamFileRepository.save(teamFile);
    }

    @Transactional(readOnly = true)
    public List<TeamFileResponseDto> getTeamFiles(Long teamSpaceId) {
        List<TeamFile> files = teamFileRepository.findByTeamSpace_TeamSpaceId(teamSpaceId);
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }
        return files.stream()
                .map(TeamFileResponseDto::from)
                .collect(Collectors.toList());
    }

    public void renameFile(Long fileId, String newFileName) {
        TeamFile teamFile = teamFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));
        teamFile.setFileName(newFileName);
    }

    public void deleteFile(Long fileId) {
        teamFileRepository.deleteById(fileId);
    }
}