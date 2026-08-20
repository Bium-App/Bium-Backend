package memo.example.demo.Exception;

/**
 * 2단계 인증 코드의 유효 시간이 지나 더 이상 사용할 수 없을 때 발생하는 예외다.
 */
public class ExpiredCodeException extends RuntimeException {
    public ExpiredCodeException(String message) {
        super(message);
    }
}