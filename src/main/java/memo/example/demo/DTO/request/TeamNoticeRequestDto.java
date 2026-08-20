package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TeamSpace 공지를 생성·수정할 때 제목, 내용과 상단 고정 여부를 전달한다.
 */
@Getter
@NoArgsConstructor
public class TeamNoticeRequestDto {
    private String title;
    private String content;
    private Boolean isPinned;
}