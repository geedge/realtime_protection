package com.realtime.protection.server.v1.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * (TCommandReport)实体类
 *
 * @author makejava
 * @since 2024-09-02 09:23:30
 */
public class TCommandReport implements Serializable {
    private static final long serialVersionUID = -51802057849045839L;
    /**
     * 唯一id
     */
    private Long id;
    /**
     * 指令id  关联
     */
    private Long commandId;
    /**
     * 关联任务id
     */
    private Long taskId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务下发人
     */
    private String userName;
    /**
     * 任务下发部门
     */
    private String department;
    /**
     * 处置事件类型
     */
    private String eventType;
    /**
     * 影响的范围
     */
    private String incidence;
    /**
     * 事件等级
     * 一般
     */
    private String eventLevel;
    /**
     * 事件起因
     */
    private String eventCause;
    /**
     * 事件造成的影响
     */
    private String eventImpact;
    /**
     * 采取的处置措施
     */
    private String disposalMeasures;
    /**
     * 处置开始时间
     */
    private Date taskStartTime;
    /**
     * 处置结束时间，空为一直生效
     */
    private Date taskEndTime;
    /**
     * 处置结果
     */
    private String disposalResults;
    /**
     * 整改措施
     */
    private String rectificationMeasures;
    /**
     * 报告创建时间
     */
    private Date createTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommandId() {
        return commandId;
    }

    public void setCommandId(Long commandId) {
        this.commandId = commandId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    public String getEventCause() {
        return eventCause;
    }

    public void setEventCause(String eventCause) {
        this.eventCause = eventCause;
    }

    public String getEventImpact() {
        return eventImpact;
    }

    public void setEventImpact(String eventImpact) {
        this.eventImpact = eventImpact;
    }

    public String getDisposalMeasures() {
        return disposalMeasures;
    }

    public void setDisposalMeasures(String disposalMeasures) {
        this.disposalMeasures = disposalMeasures;
    }

    public Date getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(Date taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public Date getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(Date taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public String getDisposalResults() {
        return disposalResults;
    }

    public void setDisposalResults(String disposalResults) {
        this.disposalResults = disposalResults;
    }

    public String getRectificationMeasures() {
        return rectificationMeasures;
    }

    public void setRectificationMeasures(String rectificationMeasures) {
        this.rectificationMeasures = rectificationMeasures;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}

