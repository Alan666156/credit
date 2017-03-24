package com.credit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by fuhongxing on 16/11/27.
 */
//@Configuration
//@EnableAutoConfiguration
@SpringBootApplication
@MapperScan("com.credit.dao")
public class CreditApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CreditApplication.class, args);
    }

}







