package com.realtime.protection.server.whitelist;

import com.realtime.protection.ProtectionApplicationTests;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class WhiteListServiceTest extends ProtectionApplicationTests {

    private final WhiteListService whiteListService;
    private WhiteListObject whiteListObject;

    @Autowired
    WhiteListServiceTest(WhiteListService whiteListService) {
        this.whiteListService = whiteListService;
    }

    @BeforeEach
    void setUp() {
        whiteListObject = new WhiteListObject();
        whiteListObject.setWhiteListName("test");
        whiteListObject.setWhiteListSystemName("china");
        whiteListObject.setWhiteListIP("128.1.1.1");
        whiteListObject.setWhiteListPort(80);
        whiteListObject.setWhiteListUrl("www.baidu.com");
        whiteListObject.setWhiteListProtocol("TCP");
    }

    @Test
    void testNewWhiteList() {
        WhiteListObject object = new WhiteListObject();
        object.setWhiteListName("test");

        Integer objectId = whiteListService.newWhiteListObject(object);
        assertTrue(objectId > 0);
    }

    @Test
    void newProtectObjects() {
        List<WhiteListObject> whiteListObjects = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            whiteListObjects.add(whiteListObject);
        }
        Boolean success = whiteListService.newWhiteListObjects(whiteListObjects);
        assertTrue(success);
    }

    @Test
    void testUpdateWhiteList() {
        WhiteListObject object = new WhiteListObject();
        //object.setWhiteListId(7);
        object.setWhiteListName("test_update");

        whiteListService.updateWhiteListObject(7, object);
    }

    @Test
    void testExistWhiteList() {
        List<Integer> ruleIds = List.of(6, 7, 8);
//        List<String> ip_list = whiteListService.existWhiteListObject(ruleIds);
//        System.out.println(ip_list);
    }

    @Test
    void testUpdateWhiteListAuditStatus() {
       //whiteListService.updateWhiteListObjectAuditStatus(7, 1);
    }

    @Test
    void testWhiteListCommandJudge() {
        FiveTupleWithMask fiveTupleWithMask = new FiveTupleWithMask();
        TaskCommandInfo taskCommandInfo = new TaskCommandInfo();
        fiveTupleWithMask.setDestinationIP("128.1.1.123");
        fiveTupleWithMask.setMaskDestinationIP("255.255.255.0");
        fiveTupleWithMask.setDestinationPort("80");
        taskCommandInfo.setFiveTupleWithMask(fiveTupleWithMask);

        List<WhiteListObject> whitelists = whiteListService.whiteListCommandJudge(taskCommandInfo);
        System.out.println(whitelists);
    }

}