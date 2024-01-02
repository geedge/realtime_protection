package com.realtime.protection.server.whitelist;

import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import com.realtime.protection.configuration.response.ResponseResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/whiteobj")
public class WhiteListController {

    private final WhiteListService whiteListService;

    public WhiteListController(WhiteListService whiteListService) {
        this.whiteListService = whiteListService;
    }

    @RequestMapping("/new")
    public ResponseResult newWhitelistObject(@RequestBody WhiteListObject object) {
        Integer whiteListObjectId = whiteListService.newWhiteListObject(object);

        return ResponseResult.ok()
                .setData("whiteobj_name", object.getWhiteListName())
                .setData("whiteobj_id", whiteListObjectId)
                .setData("success", true);
    }
}
