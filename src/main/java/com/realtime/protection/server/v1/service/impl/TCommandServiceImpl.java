package com.realtime.protection.server.v1.service.impl;

import com.realtime.protection.server.v1.entity.TCommandTask;
import com.realtime.protection.server.v1.mapper.TCommandTaskMapper;
import com.realtime.protection.server.v1.service.TCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TCommandServiceImpl implements TCommandService {


    @Autowired
    private TCommandTaskMapper tCommandTaskMapper;

    @Override
    public Long countCommandTasks(TCommandTask tCommandTask) {
        return tCommandTaskMapper.count(tCommandTask);
    }

    @Override
    public List<TCommandTask> queryCommandTasks(TCommandTask tCommandTask, Integer pageNum, Integer pageSize) {
        return tCommandTaskMapper.queryAllByLimit(tCommandTask, pageNum, pageSize);
    }

    @Override
    public Long countCommandTasks1(TCommandTask tCommandTask) {
        return tCommandTaskMapper.count1(tCommandTask);
    }

    @Override
    public List<TCommandTask> queryCommandTasks1(TCommandTask tCommandTask, Integer pageNum, Integer pageSize) {
        return tCommandTaskMapper.queryAllByLimit1(tCommandTask, pageNum, pageSize);
    }

    @Override
    public Integer resultUpdate(TCommandTask tCommandTask) {
        return tCommandTaskMapper.update(tCommandTask);
    }

    @Override
    public Integer resultDel(Long id) {
        return tCommandTaskMapper.updateResult(id);
    }
}
