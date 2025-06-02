package trade.collector.mscollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MsCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsCollectorApplication.class, args);
    }
}
