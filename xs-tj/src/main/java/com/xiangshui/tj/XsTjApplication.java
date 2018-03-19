package com.xiangshui.tj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;


@EnableWebSocket
@SpringBootApplication
public class XsTjApplication {

    public static void main(String[] args) {
        SpringApplication.run(XsTjApplication.class, args);
    }
}
