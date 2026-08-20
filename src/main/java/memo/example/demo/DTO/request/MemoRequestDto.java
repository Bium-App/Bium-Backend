package memo.example.demo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메모 생성에 필요한 제목, 본문, FIRE/ICE 상태, 만료 시각과 저장 대상을 전달한다.
 */
@Getter
@NoArgsConstructor
public class MemoRequestDto {

    // 개인 메모와 TeamSpace 메모를 구분하기 위해 대상 TeamSpace ID를 전달한다.
    private Long teamSpaceId;

    private String title;
    private String content;

    // 글자 서식이 적용된 메모 본문 데이터를 함께 전달한다.
    private String richContent;

    // FIRE 또는 ICE 상태를 전달하며 값이 없으면 ICE로 처리한다.
    private String status;

    // FIRE 메모의 만료 처리를 판단할 기준 시각을 전달한다.
    private String expiredAt;
}