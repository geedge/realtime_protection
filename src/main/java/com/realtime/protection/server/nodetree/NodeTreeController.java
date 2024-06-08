package com.realtime.protection.server.nodetree;


import com.realtime.protection.configuration.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

//李福连


@RestController
@RequestMapping("/nodeTree")
@Slf4j
public class NodeTreeController {

    @Autowired
    NodeTreeService nodeTreeService;
    @GetMapping("/get")
    public ResponseResult tupoInfo(String business) throws IOException {

        List<NodeEntity> roots = nodeTreeService.get();


        if (!roots.isEmpty()) {
            return ResponseResult.ok()
                    .setData("data", roots);
        }

        return ResponseResult.error()
                .setMessage("没有查到");
    }


}
