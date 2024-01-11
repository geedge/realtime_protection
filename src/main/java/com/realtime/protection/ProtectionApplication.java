package com.realtime.protection;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ProtectionApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ProtectionApplication.class);

        // 在实际环境中应该修改为prod
        builder.application().setAdditionalProfiles("dev");

        builder.run(args);
    }

}
