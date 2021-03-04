package digital.serpiente.aws.noip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NoipApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoipApplication.class);
    }
}
