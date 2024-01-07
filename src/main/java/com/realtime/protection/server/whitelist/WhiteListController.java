package com.realtime.protection.server.whitelist;

import com.alibaba.excel.EasyExcel;
import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import com.realtime.protection.configuration.response.ResponseResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

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

    //以excel文件方式批量导入
    @PostMapping("/upload")
    public ResponseResult uploadFile(MultipartFile uploadFile) throws IOException {
        EasyExcel.read(uploadFile.getInputStream(), WhiteListObject.class,
                new WhiteListDataListener(whiteListService)).sheet().doRead();
        return ResponseResult.ok();
    }

    @RequestMapping("/query")
    public ResponseResult queryWhiteListObject(@RequestParam(value = "whiteobj_name", required = false)String whiteListName,
                                               @RequestParam(value = "page_size", required = false)Integer whiteListId,
                                               @RequestParam(value = "page", defaultValue = "1")Integer page,
                                               @RequestParam(value = "page_size", defaultValue = "10")Integer pageSize) {
        if (page <= 0 || pageSize <= 0) {
            return new ResponseResult(400, "page or page_size is invalid")
                    .setData("whiteobj_list", null);
        }
        return ResponseResult.ok()
                .setData("whiteobj_list", whiteListService.queryWhiteListObject(whiteListName, whiteListId, page, pageSize));
    }

    @RequestMapping("/{id}/query")
    public ResponseResult queryWhiteListObjectById(@PathVariable Integer id) {
        if (id <= 0) {
            return new ResponseResult(400, "id is invalid")
                    .setData("whiteobj_list", null);
        }

        WhiteListObject whiteListObject = whiteListService.queryWhiteListObjectById(id);
        if (whiteListObject == null) {
            return new ResponseResult(400, "id is invalid")
                    .setData("whiteobj_list", null);
        }
        return ResponseResult.ok()
                .setData("whiteobj_list", whiteListObject);
    }

    //根据id删除
    @DeleteMapping("/{id}/delete")
    public ResponseResult deleteWhiteListObjectById(@PathVariable Integer id) {
        if (id <= 0) {
            return new ResponseResult(400, "id is invalid")
                    .setData("whiteobj_id", id)
                    .setData("success", false);
        }
        Integer num = whiteListService.deleteWhiteListObjectById(id);
        if (num == 0) {
            return new ResponseResult(400, "id is invalid")
                    .setData("whiteobj_id", id)
                    .setData("success", false);
        }
        return ResponseResult.ok()
                .setData("whiteobj_id", id)
                .setData("success", true);
        //还要return 白名单名称吗,还要在查表看他的名称
    }


    @PostMapping("/delete")
    public ResponseResult deleteWhiteListObjects( @RequestBody List<Integer> whiteListObjIds) {
        //Post不支持body为json。。。 body只能是[9,10]

        return ResponseResult.ok()
                .setData("whiteobj_list", whiteListObjIds)
                .setData("success", whiteListService.deleteWhiteListObjects(whiteListObjIds));
    }

    @PostMapping ("/{id}/update")
    public ResponseResult updateWhiteListObject(@PathVariable Integer id,
                                                @RequestBody WhiteListObject object) {
        object.setWhiteListId(id);
        Integer num = whiteListService.updateWhiteListObject(object);
        if (num == 0) {
            return new ResponseResult(400, "id is invalid")
                    .setData("whiteobj_list", null);
        }
        return ResponseResult.ok();
    }



    //查询ip是否存在于白名单
    @PostMapping("/exist")
    public ResponseResult existWhiteListObject(@RequestBody List<Integer> ruleIds) {
        //是请求规则的id，然后判断这个id所属的ip是否在白名单中吗
        //静态应该可以，但动态的，动态是实时过来告警信息，不存储规则？ 存的话也行，那这里要区分id是静态的还是动态的
        //这里先走通静态的，要获取规则的源IP和目的IP，去白名单select看有没有(有的还有IP掩码,暂未实现)

        //返回涉及IP在白名单中的id，
        List<String> ruleInWhiteListIds = whiteListService.existWhiteListObject(ruleIds);

        return ResponseResult.ok()
                .setData("ip_list", ruleInWhiteListIds);
    }

    //修改审核状态
    @RequestMapping("/{id}/audit/{status}")
    public ResponseResult updateWhiteListObjectAuditStatus(@PathVariable Integer id,
                                                           @PathVariable Integer status) {
        if (id <= 0 || status < 0 || status > 2) {
            return new ResponseResult(400, "id or status is invalid")
                    .setData("whiteobj_id", id)
                    .setData("success", false);
        }

        return ResponseResult.ok()
                .setDataMap(whiteListService.updateWhiteListObjectAuditStatus(id, status))
                .setData("whiteobj_id", id);
    }



}
