package com.sohu.smc.schedule.core.model;

import com.smc.notify.util.JsonUtil;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/22/13
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class Schedule {
    /** 任务计划的名称，必须是唯一的，用来唯一标识一个计划 **/
    private String scheduleName;
    /** 执行该计划的系统名称，注意：该名称必须和在Discovery注册的名称一样 **/
    private String serverName;

    /** 计划执行的计划表达式，使用的是quartz框架，详情看: http://quartz-scheduler.org/ **/
    private String scheduleCron;

    /** 计划执行的策略，适用于集群服务器，有三个值：    random-随机 polling-轮询 sticky-固定 **/
    private String strategy;

    /** 计划执行的类型；适用于集群服务器，有两个值： single-只有一台服务器执行该计划 all-集群所有服务器都执行文该计划 **/
    private String scheduleType;

    /** 计划的附加信息，该消息在计划执行时会被传递到任务执行方 **/
    private String message;


    /** 计划的状态　**/
    private int status;

    public Schedule(){}

    public Schedule(String scheduleName, String serverName, String scheduleCron){
        this(scheduleName, serverName, scheduleCron, null);
    }

    public Schedule(String scheduleName, String serverName, String scheduleCron, String strategy){
        this(scheduleName, serverName, scheduleCron, strategy, null);
    }

    public Schedule(String scheduleName, String serverName, String scheduleCron, String strategy, String scheduleType){
        this(scheduleName, serverName, scheduleCron, strategy, scheduleType, -1, null);
    }

    public Schedule(String scheduleName, String serverName, String scheduleCron, String strategy, String scheduleType, int status){
        this(scheduleName, serverName, scheduleCron, strategy, scheduleType, status, null);
    }

    public Schedule(String scheduleName, String serverName, String scheduleCron, String strategy, String scheduleType, int status, String message){
        this.scheduleName = scheduleName;
        this.serverName = serverName;
        this.scheduleCron = scheduleCron;
        this.strategy = strategy;
        this.scheduleType = scheduleType;
        this.status = status;
        this.message = message;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getScheduleCron() {
        return scheduleCron;
    }

    public void setScheduleCron(String scheduleCron) {
        this.scheduleCron = scheduleCron;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String toString(){
        return JsonUtil.getJsonStringFromObject(this);
    }
}
