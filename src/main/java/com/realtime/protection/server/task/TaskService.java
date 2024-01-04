package com.realtime.protection.server.task;

import com.realtime.protection.configuration.entity.task.Task;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final SqlSessionFactory sqlSessionFactory;
    private final TaskMapper taskMapper;

    public TaskService(SqlSessionFactory sqlSessionFactory, TaskMapper taskMapper) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.taskMapper = taskMapper;
    }

    public Integer newTask(Task task) {
        SqlSession session = sqlSessionFactory.openSession(false);
        TaskMapper taskMapper = session.getMapper(TaskMapper.class);
        try {
            taskMapper.newTask(task);

            taskMapper.newTaskProtectObjectConcat(task.getTaskId(), task.getProtectObjectIds());

            taskMapper.newTaskStaticRuleConcat(task.getTaskId(), task.getStaticRuleIds());

            session.commit();
        } catch (Exception e) {
            session.rollback();
            throw e;
        } finally {
            session.close();
        }

        if (task.getTaskId() == null) {
            return 0;
        }
        return task.getTaskId();
    }

    public List<Task> queryTasks(Integer taskStatus,
                                 String taskType, String taskName, String taskCreator,
                                 Integer page, Integer pageSize) {
        return taskMapper.queryTasks(taskStatus, taskType, taskName, taskCreator, page, pageSize);
    }

    public Task queryTask(Integer id) {
        SqlSession session = sqlSessionFactory.openSession(false);
        TaskMapper taskMapper = session.getMapper(TaskMapper.class);
        Task task;

        try {
            task = taskMapper.queryTask(id);
            if (task == null) {
                return null;
            }
            task.setProtectObjectIds(taskMapper.queryTaskConcatProtectObjectIds(task.getTaskId()));
//            task.setDynamicRuleIds(taskMapper.queryTaskConcatDynamicRuleIds(task.getTaskId()));
            task.setStaticRuleIds(taskMapper.queryTaskConcatStaticRuleIds(task.getTaskId()));

            session.commit();
        } catch (Exception e) {
            session.rollback();
            throw e;
        } finally {
            session.close();
        }

        return task;
    }

    public void updateTask(Task task) {
        SqlSession session = sqlSessionFactory.openSession(false);
        TaskMapper taskMapper = session.getMapper(TaskMapper.class);
        try {
            taskMapper.updateTask(task);

            taskMapper.clearTaskProtectObjectConcat(task.getTaskId());
            taskMapper.clearTaskConnectedStaticRule(task.getTaskId());
//            taskMapper.clearTaskConnectedDynamicRule(task.getTaskId());

            if (task.getProtectObjectIds() != null && !task.getProtectObjectIds().isEmpty()) {
                taskMapper.newTaskProtectObjectConcat(task.getTaskId(), task.getProtectObjectIds());
            }

            if (task.getStaticRuleIds() != null && !task.getStaticRuleIds().isEmpty()) {
                taskMapper.newTaskStaticRuleConcat(task.getTaskId(), task.getStaticRuleIds());
            }

            if (task.getDynamicRuleIds() != null && !task.getDynamicRuleIds().isEmpty()) {
                taskMapper.newTaskDynamicRuleConcat(task.getTaskId(), task.getDynamicRuleIds());
            }

            session.commit();
        } catch (Exception e) {
            session.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
