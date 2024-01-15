package com.realtime.protection;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Rollback
@DSTransactional
public class ProtectionApplicationTests {

}
