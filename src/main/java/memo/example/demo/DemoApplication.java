package memo.example.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.util.TimeZone;

/**
 * 백그라운드 스케줄러를 활성화하고 애플리케이션 기본 시간대를 설정한다.
 */
@EnableScheduling
@SpringBootApplication
public class DemoApplication {
    // 서버 환경과 관계없이 날짜 계산에 KST를 사용한다.
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}