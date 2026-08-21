package memo.example.demo.Exception;

/**
 * 입력한 2단계 인증 코드가 올바르지 않을 때 발생하는 예외다.
 */
public class InvalidCodeException extends RuntimeException {
    public InvalidCodeException(String message) {
        super(message);
    }
}