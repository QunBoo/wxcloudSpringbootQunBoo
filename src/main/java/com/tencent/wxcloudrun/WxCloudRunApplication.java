package com.tencent.wxcloudrun;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
@MapperScan(basePackages = {"com.tencent.wxcloudrun.dao"})
public class WxCloudRunApplication {  

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(WxCloudRunApplication.class, args);
//    ConfigurableApplicationContext context = SpringApplication.run(FangZhenApplication.class, args);
    ConfigurableEnvironment env = context.getEnvironment();
    try {


      log.info("\n----------------------------------------------------------\n\t" +
                      "应用 '{}' 已启动! 访问地址:\n\t" +
                      "本地地址: \t{}://localhost:{}\n\t" +
                      "外部地址: \t{}://{}:{}\n\t" +
                      "API说明: \thttp://localhost:80/swagger-ui.html",
              "当前配置: \t{}\n----------------------------------------------------------\n\t" +

                      env.getProperty("app.name"),
              "http",
              env.getProperty("server.port"),
              "http",
              InetAddress.getLocalHost().getHostAddress(),
              env.getProperty("server.port"),
              env.getActiveProfiles());
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }

  }
}
