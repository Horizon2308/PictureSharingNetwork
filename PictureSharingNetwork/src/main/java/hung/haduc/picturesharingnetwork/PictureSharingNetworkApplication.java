package hung.haduc.picturesharingnetwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
public class PictureSharingNetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(PictureSharingNetworkApplication.class, args);
    }

}
