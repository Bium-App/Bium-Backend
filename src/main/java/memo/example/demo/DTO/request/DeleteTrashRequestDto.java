package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 한 번에 영구 삭제할 메모 ID 목록을 전달한다.
 */
@Getter
@NoArgsConstructor
public class DeleteTrashRequestDto {
    private List<Long> memoIds;
}