package com.realtime.protection.server.dictionary;

import com.realtime.protection.configuration.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/dict")
public class DictController implements DictControllerApi{

    private final DictService dictService;

    public DictController(DictService dictService) {
        this.dictService = dictService;
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @Override
    @GetMapping(value = "/type/{dictType}")
    public ResponseResult dictType(@PathVariable String dictType) {
        return ResponseResult.ok()
                .setData("dictData", dictService.selectDictDataByType(dictType));
    }

}
