package com.realtime.protection.server.whitelist;

import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import org.springframework.stereotype.Service;

@Service
public class WhiteListService {

    private final WhiteListMapper whiteListMapper;

    public WhiteListService(WhiteListMapper whiteListMapper) {
        this.whiteListMapper = whiteListMapper;
    }

    public Integer newWhiteListObject(WhiteListObject object) {

        whiteListMapper.newWhiteListObject(object);

        return object.getWhiteListId();
    }
}
