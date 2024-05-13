package com.realtime.protection.server.dictionary;

import com.realtime.protection.configuration.entity.dict.SysDictData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DictMapper {
    List<SysDictData> selectDictDataByType(String dictType);
}
