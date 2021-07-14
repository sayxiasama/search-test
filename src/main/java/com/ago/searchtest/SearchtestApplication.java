package com.ago.searchtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@ComponentScan({"com.ago.opensearch","com.ago.searchtest"})
public class SearchtestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchtestApplication.class, args);
    }

}
