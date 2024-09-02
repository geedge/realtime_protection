package com.realtime.protection.server.v1.service;

import com.realtime.protection.server.v1.entity.TCommandTask;

import java.util.List;

public interface TCommandService {

    Long countCommandTasks(TCommandTask tCommandTask);

    List<TCommandTask> queryCommandTasks(TCommandTask tCommandTask, Integer pageNum, Integer pageSize);

    Long countCommandTasks1(TCommandTask tCommandTask);

    List<TCommandTask> queryCommandTasks1(TCommandTask tCommandTask, Integer pageNum, Integer pageSize);

}
