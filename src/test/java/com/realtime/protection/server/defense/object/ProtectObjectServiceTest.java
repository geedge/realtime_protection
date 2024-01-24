package com.realtime.protection.server.defense.object;

import com.realtime.protection.ProtectionApplicationTests;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProtectObjectServiceTest extends ProtectionApplicationTests {

    private final ProtectObjectService protectObjectService;
    private ProtectObject protectObject;

    @Autowired
    ProtectObjectServiceTest(ProtectObjectService protectObjectService) {
        this.protectObjectService = protectObjectService;
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

        assertThrows(DataIntegrityViolationException.class, () -> assertTrue(protectObjectService.newProtectObject(protectObject) > 0));
    }

    @Test
    void newProtectObjects() {
        List<ProtectObject> protectObjects = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            protectObjects.add(protectObject);
        }

        Boolean success = protectObjectService.newProtectObjects(protectObjects);
        assertTrue(success);
    }

    @Test
    void queryProtectObject() {
        List<ProtectObject> protectObjects = new ArrayList<>();
        String testName = "xxx" + LocalDateTime.now();
        protectObject.setProtectObjectName(testName);
        for (int i = 0; i < 100; i++) {
            protectObject.setProtectObjectId(null);
            protectObjectService.newProtectObject(protectObject);
            protectObjects.add(protectObject);
        }


        protectObjects.forEach(protectObject -> {
                assertNotNull(protectObjectService.queryProtectObject(protectObject.getProtectObjectId()));
                assertNotNull(protectObjectService.queryProtectObjects(null,
                        protectObject.getProtectObjectId(), 1, 1));
        });

        assertEquals(100,
                protectObjectService.queryProtectObjects(testName, null, 1, 1000).size());

    }

    @Test
    void updateProtectObject() {
        List<ProtectObject> protectObjects = protectObjectService.queryProtectObjects(null, null, 1, 1);
        Integer testId = protectObjects.get(0).getProtectObjectId();

        protectObject.setProtectObjectName("x-1-1");
        protectObject.setProtectObjectId(testId);
        assertTrue(protectObjectService.updateProtectObject(protectObject));
        assertEquals("x-1-1", protectObjectService.queryProtectObject(testId).getProtectObjectName());
    }

    @Test
    void deleteProtectObject() {
        int testNum = 0;
        for (int i = 0; i < 100000; i++) {
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
        ArrayList<Integer> testNums = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            if (protectObjectService.queryProtectObject(i) != null) {
                testNums.add(i);
                if (testNums.size() > 5) {
                    break;
                }
            }
        }

        assertTrue(protectObjectService.deleteProtectObjects(testNums));
        for (Integer testNum : testNums) {
            assertNull(protectObjectService.queryProtectObject(testNum));
        }
    }

    @Test
    void changeProtectObjectAuditStatus() {
        int testNum = 0;
        for (int i = 300; i < 100000; i++) {
            if (protectObjectService.queryProtectObject(i) != null) {
                testNum = i;
                break;
            }
        }
        assertTrue((Boolean) protectObjectService.changeProtectObjectAuditStatus(testNum, 1).get("success"));
        int finalTestNum = testNum;
        assertThrows(IllegalArgumentException.class, () -> protectObjectService.changeProtectObjectAuditStatus(finalTestNum, 2));
    }
}