package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기존 메모에서 변경할 제목, 일반 본문 또는 Rich Content를 전달한다.
 */
@Getter
@NoArgsConstructor
public class MemoUpdateRequestDto {
    private String title;
    private String content;
    // 값이 전달된 경우에만 글자 서식이 포함된 본문을 변경한다.
    private String richContent;
}