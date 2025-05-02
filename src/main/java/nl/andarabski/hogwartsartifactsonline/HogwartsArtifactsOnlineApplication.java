package nl.andarabski.hogwartsartifactsonline;

import nl.andarabski.hogwartsartifactsonline.artifact.utils.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HogwartsArtifactsOnlineApplication {

    public static void main(String[] args) {

        SpringApplication.run(HogwartsArtifactsOnlineApplication.class, args);
    }
//https://www.youtube.com/watch?v=7kgbEHGOfNg&ab_channel=DEEJAYFAMILYmegamixes

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(1, 1);
    }
}
