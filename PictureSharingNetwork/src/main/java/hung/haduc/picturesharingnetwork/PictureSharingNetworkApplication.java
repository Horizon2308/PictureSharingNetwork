package hung.haduc.picturesharingnetwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
@SpringBootApplication
public class PictureSharingNetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(PictureSharingNetworkApplication.class, args);
    }

}
