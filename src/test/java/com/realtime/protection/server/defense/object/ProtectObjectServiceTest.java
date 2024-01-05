package com.realtime.protection.server.defense.object;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class ProtectObjectServiceTest {

    private final ProtectObjectService protectObjectService;

    @Autowired
    ProtectObjectServiceTest(ProtectObjectService protectObjectService) {
        this.protectObjectService = protectObjectService;
    }

    @Test
    void testDeleteProtectObject() {
        Boolean success = protectObjectService.deleteProtectObjects(List.of(1, 2, 3, 4));
        assertFalse(success);
    }
}