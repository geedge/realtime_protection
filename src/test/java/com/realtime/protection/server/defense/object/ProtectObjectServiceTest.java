package com.realtime.protection.server.defense.object;

import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import org.apache.ibatis.exceptions.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProtectObjectServiceTest {

    private final ProtectObjectService protectObjectService;
    private ProtectObject protectObject;

    @Autowired
    ProtectObjectServiceTest(ProtectObjectService protectObjectService) {
        this.protectObjectService = protectObjectService;
    }

    @Test
    void testDeleteProtectObject() {
        Boolean success = protectObjectService.deleteProtectObjects(List.of(1, 2, 3, 4));
        assertFalse(success);
    }

    @BeforeEach
    void setUp() {
        protectObject = new ProtectObject();
        protectObject.setProtectObjectName("x-2-2");
        protectObject.setProtectObjectSystemName("xxxx system");
        protectObject.setProtectObjectIPAddress("192.168.1.4");
        protectObject.setProtectObjectPort(11);
        protectObject.setProtectObjectURL("alice.bob.com");
        protectObject.setProtectObjectProtocol("UDP");
    }

    @Test
    void newProtectObject() {
        assertTrue(protectObjectService.newProtectObject(protectObject) > 0);

        protectObject.setProtectObjectId(null);
        protectObject.setProtectObjectProtocol(null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            assertTrue(protectObjectService.newProtectObject(protectObject) > 0);
        });
    }

    @Test
    void newProtectObjects() {
        List<ProtectObject> protectObjects = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            protectObjects.add(protectObject);
        }

        Boolean success = protectObjectService.newProtectObjects(protectObjects);
        assertTrue(success);
    }

    @Test
    void updateProtectObject() {
        protectObject.setProtectObjectName("x-1-1");
        protectObject.setProtectObjectId(10);
        assertTrue(protectObjectService.updateProtectObject(protectObject));
        assertEquals("x-1-1", protectObjectService.queryProtectObject(10).getProtectObjectName());
    }

    @Test
    void deleteProtectObject() {
        int testNum = 0;
        for (int i = 0; i < 100000; i++ ) {
            if (protectObjectService.queryProtectObject(i) != null) {
                testNum = i;
                break;
            }
        }

        assertTrue(protectObjectService.deleteProtectObject(testNum));
        assertNull(protectObjectService.queryProtectObject(testNum));
    }

    @Test
    void deleteProtectObjects() {
        assertTrue(protectObjectService.deleteProtectObjects(List.of(270, 271)));
        assertNull(protectObjectService.queryProtectObject(270));
        assertNull(protectObjectService.queryProtectObject(271));
    }

    @Test
    void changeProtectObjectAuditStatus() {
        int testNum = 0;
        for (int i = 300; i < 100000; i++ ) {
            if (protectObjectService.queryProtectObject(i) != null) {
                testNum = i;
                break;
            }
        }
        assertTrue((Boolean) protectObjectService.changeProtectObjectAuditStatus(testNum, 1).get("success"));
        int finalTestNum = testNum;
        assertThrows(IllegalArgumentException.class, () -> {
            protectObjectService.changeProtectObjectAuditStatus(finalTestNum, 2);
        });
    }
}