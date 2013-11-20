package com.sohu.smc.schedule.core.service;

import com.sohu.smc.schedule.core.exception.SmcSchedulerException;
import com.sohu.smc.schedule.core.model.Schedule;
import com.sohu.smc.schedule.core.util.SchedulerHolder;
import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/22/13
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class SchedulerService {
    static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);
    static SchedulerService instance = new SchedulerService();

    public boolean addSchedule(String scheduleName, String serverName, String scheduleCron, String scheduleType, String strategy) throws SmcSchedulerException {
        try {
            JobKey jobKey = new JobKey(scheduleName, serverName);
            JobDetail job = SchedulerHolder.scheduler.getJobDetail(jobKey);
            if (job != null) {
                throw new SmcSchedulerException("该计划已经存在，请核实后再提交！");
            }

            CronExpression cronExpression = null;

            try {
                cronExpression = new CronExpression(scheduleCron);
            } catch (ParseException e) {
                throw new SmcSchedulerException("计划表达式不正确，请核实后再提交！");
            }
            StringBuilder sb = new StringBuilder(scheduleType);
            sb.append("@").append(strategy);
            job = newJob(ScheduleJob.class).withIdentity(scheduleName, serverName)
                    .withDescription(sb.toString()).storeDurably().build();

            CronTrigger trigger = newTrigger().withIdentity(scheduleName, serverName).forJob(job).withSchedule(cronSchedule(cronExpression)).build();

            Date startTime = SchedulerHolder.scheduler.scheduleJob(job, trigger);
            SchedulerHolder.scheduler.pauseJob(jobKey);
//           SchedulerHolder.scheduler.pauseJob(new JobKey(scheduleName, serverName));
            System.out.println("计划创建成功！计划名称：" + scheduleName + "，第一次执行时间为：" + trigger.getNextFireTime());
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new SmcSchedulerException("创建过程发生错误！错误信息：" + e.getMessage());
        }

    }

    public boolean modifySchedule(String scheduleName, String serverName, String scheduleCron, String scheduleType, String strategy) throws SmcSchedulerException {
        try {
            JobKey jobKey = new JobKey(scheduleName, serverName);
            JobDetail job = SchedulerHolder.scheduler.getJobDetail(jobKey);
            if (job == null) {
                throw new SmcSchedulerException("该计划不存在，请核实后再提交！");
            }

            CronExpression cronExpression = null;

            try {
                cronExpression = new CronExpression(scheduleCron);
            } catch (ParseException e) {
                e.printStackTrace();
                throw new SmcSchedulerException("计划表达式不正确，请核实后再提交！");
            }
            boolean isSucc = this.removeSchedule(scheduleName, serverName);
            if(isSucc){
                return this.addSchedule(scheduleName, serverName, scheduleCron, scheduleType, strategy);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new SmcSchedulerException("过程发生错误！错误信息：" + e.getMessage());
        }
        return false;

    }
    public boolean removeSchedule(String scheduleName, String serverName) throws SmcSchedulerException {
        try {
            JobKey jobKey = new JobKey(scheduleName, serverName);
            JobDetail job = SchedulerHolder.scheduler.getJobDetail(jobKey);
            if (job == null) {
                throw new SmcSchedulerException("该计划不存在，请核实后再执行该操作！");
            } else {
                boolean isSucc = SchedulerHolder.scheduler.deleteJob(jobKey);
                if (!isSucc) {
                    throw new SmcSchedulerException("删除该计划出错！请稍后再试！");
                }

            }

        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new SmcSchedulerException("删除该计划出错！错误信息：" + e.getMessage());
        }
        return true;
    }

    public boolean startSchedule(String scheduleName, String serverName) throws SmcSchedulerException {
        try {
            JobKey jobKey = new JobKey(scheduleName, serverName);
            JobDetail job = SchedulerHolder.scheduler.getJobDetail(jobKey);
            if (job == null) {
                throw new SmcSchedulerException("该计划不存在，请核实后再执行该操作！");
            } else {
                Trigger trigger = SchedulerHolder.scheduler.getTrigger(new TriggerKey(scheduleName, serverName));
                if (trigger == null) {
                    throw new SmcSchedulerException("该计划不存在执行计划，请先添加执行计划！");
                } else {
                    boolean isStarted = SchedulerHolder.scheduler.isStarted();
                    if (!isStarted) {
                        SchedulerHolder.scheduler.start();
                    }
                    if (SchedulerHolder.scheduler.checkExists(jobKey)) {
                        SchedulerHolder.scheduler.resumeJob(jobKey);
                        SchedulerHolder.scheduler.resumeTrigger(new TriggerKey(scheduleName, serverName));
                    } else {
                        SchedulerHolder.scheduler.scheduleJob(job, trigger);
                    }
                }

            }


        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new SmcSchedulerException("开始计划出错，错误信息：" + e.getMessage());
        }
        return true;

    }

    public boolean pauseSchedule(String scheduleName, String serverName) throws SmcSchedulerException {
        try {
            JobKey jobKey = new JobKey(scheduleName, serverName);
            JobDetail job = SchedulerHolder.scheduler.getJobDetail(jobKey);
            if (job == null) {
                throw new SmcSchedulerException("该计划不存在，请核实后再执行该操作！");
            } else {
                SchedulerHolder.scheduler.pauseJob(jobKey);
                SchedulerHolder.scheduler.pauseTrigger(new TriggerKey(scheduleName, serverName));
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new SmcSchedulerException("停止计划出错，错误信息：" + e.getMessage());
        }
        return true;
    }

    public List<Schedule> listSchedule() {
        List<Schedule> schedules = new ArrayList<Schedule>();
        try {
            Set<JobKey> jobKeySet = SchedulerHolder.scheduler.getJobKeys(GroupMatcher.<JobKey>anyGroup());
            if(jobKeySet != null && jobKeySet.size() > 0){
                for(JobKey each : jobKeySet){
                    Schedule schedule = getSchedule(each);
                    schedules.add(schedule);
                }

            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return schedules;
    }

    public Schedule getSchedule(JobKey each) {
        JobDetail job = null;
        try {
            job = SchedulerHolder.scheduler.getJobDetail(each);
        String desc = job.getDescription();
        String[] split = StringUtils.split(desc, "@");
        String schduleType = null;
        String strategy = null;
        if(split.length == 2){
            schduleType = split[0];
            strategy = split[1];
        }
        String cronExpression = null;
        Trigger trigger = SchedulerHolder.scheduler.getTrigger(new TriggerKey(each.getName(), each.getGroup()));
        if(trigger instanceof CronTrigger){
            CronTrigger cronTrigger = (CronTrigger)trigger;
            cronExpression = cronTrigger.getCronExpression().toString();
        }
        Trigger.TriggerState state = SchedulerHolder.scheduler.getTriggerState(trigger.getKey());
        int status = -1;
        switch(state){
            case NONE : {
                status = 0; break;
            }
            case NORMAL:{
                status = 1; break;
            }
            case PAUSED:{
                status = 2; break;
            }
            case COMPLETE:{
                status = 3; break;
            }
            case ERROR:{
                status = 4; break;
            }
        }
            return new Schedule(each.getName(), each.getGroup(), cronExpression, strategy, schduleType, status);
        } catch (SchedulerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    private SchedulerService() {
    }

    public static SchedulerService getInstance() {
        return instance;
    }

}