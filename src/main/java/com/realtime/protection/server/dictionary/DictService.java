package com.realtime.protection.server.dictionary;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.realtime.protection.configuration.entity.dict.SysDictData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@DS("mysql")
public class DictService {
    private final DictMapper dictMapper;

    public DictService(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    public List<SysDictData> selectDictDataByType(String dictType) {
        return dictMapper.selectDictDataByType(dictType);
    }
}
