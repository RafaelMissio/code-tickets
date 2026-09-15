package br.com.missio.codeticks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CodeticksApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeticksApplication.class, args);
    }

}
