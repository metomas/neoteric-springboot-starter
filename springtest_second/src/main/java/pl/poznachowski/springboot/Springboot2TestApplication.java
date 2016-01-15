package pl.poznachowski.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class Springboot2TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(Springboot2TestApplication.class, args);
    }
}
