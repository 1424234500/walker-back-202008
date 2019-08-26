package com.walker;

import com.mchange.v2.c3p0.jboss.C3P0PooledDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
