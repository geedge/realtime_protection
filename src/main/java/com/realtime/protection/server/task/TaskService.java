package com.realtime.protection.server.task;

import com.realtime.protection.configuration.entity.task.Task;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskService {

    private final SqlSessionFactory sqlSessionFactory;

    public TaskService(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public Integer newTask(Task task) {
        task.setTaskCreateTime(LocalDateTime.now());
        task.setTaskModifyTime(LocalDateTime.now());

        SqlSession session = sqlSessionFactory.openSession(false);
        TaskMapper taskMapper = session.getMapper(TaskMapper.class);
        try {
            taskMapper.newTask(task);

            taskMapper.newTaskProobjConcat(task.getTaskId(), task.getProtectObjectIds());


//            if (taskMapper.newTaskStaticRuleConcat(task.getTaskId(), task.getStaticRuleIds())
//                    != task.getStaticRuleIds().length)
//                throw new Exception("update lines is not equal to static_rule_ids size");

//            if (taskMapper.newTaskDynamicRuleConcat(task.getTaskId(), task.getDynamicRuleIds())
//                    != task.getDynamicRuleIds().length)
//                throw new Exception("update lines is not equal to dynamic_rule_ids size");

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
}
