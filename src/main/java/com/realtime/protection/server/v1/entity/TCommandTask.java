package com.realtime.protection.server.v1.entity;

import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * (TCommandTask)实体类
 *
 * @author makejava
 * @since 2024-08-27 15:36:47
 */
public class TCommandTask implements Serializable {
    private static final long serialVersionUID = -38138465113282389L;
    /**
     * 任务id  关联任务表
     */
    private Long taskId;
    /**
     * 指挥指令id
     */
    private Long id;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 处置事件类型
     */
    private String eventType;
    /**
     * 影响的范围
     */
    private String incidence;
    /**
     * 事件等级 字典：特别重大  重大  较大 一般
     */
    private String eventLevel;
    /**
     * 采取的处置措施
     */
    private String disposalMeasures;
    /**
     * 处置开始时间
     */
    private String taskStartTime;
    /**
     * 处置结束时间 空为一直生效
     */
    private String taskEndTime;
    /**
     * 处置优先级 字典：高  中 低
     */
    private String priority;
    /**
     * 来函附件，暂定文件路径
     */
    private String file;
    /**
     * 入库时间
     */
    private Date taskCreateTime;
    /**
     * 任务创建人名称
     */
    private String taskCreateUsername;
    /**
     * 任务创建人处室
     */
    private String taskCreateDepart;
    /**
     * 显示ID
     */
    private String taskDisplayId;


    /**
     * todo 处置任务ID（暂未使用）
     */
    private String taskUuid;

    /**
     * 处置结果
     */
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getIncidence() {
        return incidence;
    }

    public void setIncidence(String incidence) {
        this.incidence = incidence;
    }

    public String getEventLevel() {
        return eventLevel;
    }

    public void setEventLevel(String eventLevel) {
        this.eventLevel = eventLevel;
    }

    public String getDisposalMeasures() {
        return disposalMeasures;
    }

    public void setDisposalMeasures(String disposalMeasures) {
        this.disposalMeasures = disposalMeasures;
    }

    public String getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(String taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public String getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(String taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Date getTaskCreateTime() {
        return taskCreateTime;
    }

    public void setTaskCreateTime(Date taskCreateTime) {
        this.taskCreateTime = taskCreateTime;
    }

    public String getTaskCreateUsername() {
        return taskCreateUsername;
    }

    public void setTaskCreateUsername(String taskCreateUsername) {
        this.taskCreateUsername = taskCreateUsername;
    }

    public String getTaskCreateDepart() {
        return taskCreateDepart;
    }

    public void setTaskCreateDepart(String taskCreateDepart) {
        this.taskCreateDepart = taskCreateDepart;
    }

    public String getTaskDisplayId() {
        return taskDisplayId;
    }

    public void setTaskDisplayId(String taskDisplayId) {
        this.taskDisplayId = taskDisplayId;
    }

    public String getTaskUuid() {
        return taskUuid;
    }

    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
    }
}

