package com.walker;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.walker.dao.mapper")
public class ApplicationWeb {

    public static void main(String[] args) {
        try {
            SpringApplication.run(ApplicationWeb.class, args);
        }catch (UnsatisfiedDependencyException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
