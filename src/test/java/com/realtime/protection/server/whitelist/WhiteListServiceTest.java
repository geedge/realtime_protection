package com.realtime.protection.server.whitelist;

import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WhiteListServiceTest {

    private final WhiteListService whiteListService;

    @Autowired
    WhiteListServiceTest(WhiteListService whiteListService) {
        this.whiteListService = whiteListService;
    }

    @Test
    void testNewWhiteList() {
        WhiteListObject object = new WhiteListObject();
        object.setWhiteListName("test");

        Integer objectId = whiteListService.newWhiteListObject(object);
        assertTrue(objectId > 0);
    }
}