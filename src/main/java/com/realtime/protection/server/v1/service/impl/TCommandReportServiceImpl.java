package com.realtime.protection.server.v1.service.impl;

import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.server.task.TaskMapper;
import com.realtime.protection.server.v1.entity.TCommandReport;
import com.realtime.protection.server.v1.entity.TCommandTask;
import com.realtime.protection.server.v1.mapper.TCommandReportMapper;
import com.realtime.protection.server.v1.mapper.TCommandTaskMapper;
import com.realtime.protection.server.v1.service.TCommandReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Service
public class TCommandReportServiceImpl implements TCommandReportService {

    @Autowired
    private TCommandReportMapper tCommandReportMapper;
    @Autowired
    private TCommandTaskMapper tCommandTaskMapper;
    @Autowired
    private TaskMapper taskMapper;

    @Override
    public Integer infoUpdate(TCommandReport tCommandReport) {
        return tCommandReportMapper.update(tCommandReport);
    }

    @Override
    public TCommandReport queryInfo(Long id) {
        return tCommandReportMapper.queryByCommandId(id);
    }

    @Override
    public Integer infoAdd(TCommandReport tCommandReport) {
        TCommandTask tCommandTask = tCommandTaskMapper.queryById(tCommandReport.getCommandId());
        Task task = taskMapper.queryTask(tCommandTask.getTaskId());
        if (task == null) {
            return 0;
        }
        tCommandReport.setTaskId(tCommandTask.getTaskId());
        tCommandReport.setTaskName(task.getTaskName());
        tCommandReport.setUserName(task.getTaskCreateUsername());
        tCommandReport.setDepartment(task.getTaskCreateDepart());
        tCommandReport.setEventLevel(tCommandTask.getEventLevel());
        tCommandReport.setEventType(tCommandTask.getEventType());
        tCommandReport.setIncidence(tCommandTask.getIncidence());
        if (task.getTaskCreateTime() != null) {
            ZonedDateTime zonedDateTime = task.getTaskCreateTime().atZone(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            Date date = Date.from(instant);
            tCommandReport.setCreateTime(date);
        }
        if (task.getTaskStartTime() != null) {
            ZonedDateTime zonedDateTime = task.getTaskStartTime().atZone(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            Date date = Date.from(instant);
            tCommandReport.setTaskStartTime(date);
        }
        if (task.getTaskEndTime() != null) {
            ZonedDateTime zonedDateTime = task.getTaskEndTime().atZone(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            Date date = Date.from(instant);
            tCommandReport.setTaskEndTime(date);
        }
        return tCommandReportMapper.insert(tCommandReport);
    }
}
