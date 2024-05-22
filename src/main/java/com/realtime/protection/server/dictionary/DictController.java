package com.realtime.protection.server.dictionary;

import com.realtime.protection.configuration.response.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
