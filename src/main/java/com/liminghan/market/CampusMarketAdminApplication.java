package com.liminghan.market;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.liminghan.market.mapper")
@SpringBootApplication
public class CampusMarketAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusMarketAdminApplication.class, args);
    }
}
