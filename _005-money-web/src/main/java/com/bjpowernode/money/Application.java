package com.bjpowernode.money;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ：@EnableDubboConfiguration：在消费端，启动类中配上@EnableDubboConfiguration，
 *                              调用的时候配上@Reference注入依赖，就可以调用
 */
@Slf4j  //输出日志，lombok包
@SpringBootApplication  ////启动springboot内嵌的tomcat
@EnableDubboConfiguration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        //@Slf4j注解方法
        log.info("005启动成功...");
    }

}
