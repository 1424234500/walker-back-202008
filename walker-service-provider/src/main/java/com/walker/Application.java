package com.walker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.out.println("start - ----------------------------");
        System.out.println("start - ----------------------------");
        System.out.println("start - ----------------------------");
        SpringApplication.run(Application.class, args);


        System.out.println("stop - ----------------------------");
    }

}
