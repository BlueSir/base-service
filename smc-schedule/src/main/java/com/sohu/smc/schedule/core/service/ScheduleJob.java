package com.sohu.smc.schedule.core.service;

import com.netflix.curator.x.discovery.ServiceInstance;
import com.nsq.Message;
import com.nsq.MessageType;
import com.nsq.exceptions.BadMessageException;
import com.nsq.exceptions.BadTopicException;
import com.nsq.exceptions.DisconnectedException;
import com.nsq.exceptions.NoConnectionsException;
import com.smc.notify.Notify;
import com.sohu.smc.schedule.core.model.ScheduleType;
import com.sohu.smc.schedule.core.model.Strategy;
import com.sohu.smc.schedule.core.util.DiscoveryUtil;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/24/13
 * Time: 8:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScheduleJob implements Job {
    static final Logger LOG = LoggerFactory.getLogger(ScheduleJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if(context == null) return;
        JobDetail jobDetail = context.getJobDetail();
        if(jobDetail == null) return;
        String scheduleName = jobDetail.getKey().getName();
        String serverName = jobDetail.getKey().getGroup();
        String desc = jobDetail.getDescription();
        String[] split = StringUtils.split(desc, "@");
        ScheduleType schduleType = null;
        Strategy strategy = null;
        if (split.length == 2) {
            schduleType = ScheduleType.getScheduleTypeByName(split[0]);
            strategy = Strategy.getStrategyByName(split[1]);
        }
        if (schduleType == null) {
            LOG.error("Schedule type is empty.scheduleName=" + scheduleName + ",serverName=" + serverName);
            return;
        }
        Message message = null;
        List<ServiceInstance<String>> allService = DiscoveryUtil.getAllService(serverName, true);
        if(allService == null || allService.size() <= 0) return;
        switch (schduleType) {
            case SINGLE: {
                ServiceInstance<String> server = DiscoveryUtil.getService(serverName, (strategy == null ? Strategy.POLLING : strategy));
                if(server == null){
                    LOG.error(serverName + " hasn't instance.scheduleName=" + scheduleName);
                    return;
                }else{
                    message = new Message(MessageType.SCHEDULE_SINGLE_EXECUTE, server.getAddress(), server.getPort());
                }
                break;
            }
            case ALL: {
                message = new Message(MessageType.SCHEDULE_ALL_EXECUTE, Notify.identification);
                break;
            }
        }
        try {
            LOG.info("[ScheduleJob.execute]:scheduleName=" + scheduleName + ",serverName="+serverName +",schduleType=" + schduleType +",strategy="+strategy+",message="+message);
            Notify.producer.produce("SCHEDULE_"+scheduleName, message.toString().getBytes());
        } catch (DisconnectedException e) {
            e.printStackTrace();
        } catch (BadTopicException e) {
            e.printStackTrace();
        } catch (BadMessageException e) {
            e.printStackTrace();
        } catch (NoConnectionsException e) {
            e.printStackTrace();
        }

    }
}
