package com.realtime.protection;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ProtectionApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ProtectionApplication.class);

        // 在实际环境中应该修改为prod
        builder.application().setAdditionalProfiles("test");

        builder.run(args);
    }

}
