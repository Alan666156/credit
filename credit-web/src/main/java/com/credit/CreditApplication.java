package com.credit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * Created by fuhongxing on 16/11/27.
 */
@Configuration
@EnableAutoConfiguration
public class CreditApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CreditApplication.class, args);
    }

}







