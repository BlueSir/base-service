package com.sohu.smc.schedule.core.service;

import com.netflix.curator.x.discovery.ServiceInstance;
import com.nsq.ConsumerListener;
import com.nsq.Consumers;
import com.nsq.Message;
import com.nsq.MessageType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/22/13
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmcSchedule {
    private static final Logger LOG = LoggerFactory.getLogger(SmcSchedule.class);

    /**
     * 执行分页式任务，并为其增加监听器
     * @param scheduleName 任务的名称，跟任务系统上注册的相同
     * @param scheduleListener 任务执行的监听器，具体业务就是在这里实现的
     */
    public static void execute(String scheduleName, final ScheduleListener scheduleListener){
        execute(scheduleName, scheduleListener, 3);
    }

    /**
     * 执行分页式任务，并为其增加监听器
     * @param scheduleName 任务的名称，跟任务系统上注册的相同
     * @param scheduleListener 任务执行的监听器，具体业务就是在这里实现的
     * @param retry 如果业务执行执行失败，可以重试的次数
     */
    public static void execute(String scheduleName, final ScheduleListener scheduleListener, int retry){
        Consumers.registerTopic("SCHEDULE_" + scheduleName, new ConsumerListener() {
            @Override
            public boolean excute(Message message) {
                String type = message.getT();
                MessageType messageType = MessageType.getMessageTypeByCode(type);
                if (messageType == null) {
                    LOG.error("Message type is not defined.messageType=" + type);
                    return true;
                }
                String host = message.getH();
                int port = message.getP();
                String key = message.getM();
                switch (messageType) {
                    case SCHEDULE_SINGLE_EXECUTE: {
                        ServiceInstance<String> instance = SmcDiscovery.getServiceInstance();
                        if (instance == null) {
                            LOG.error("Discovery not been register.Please invoke SmcDiscovery.register method to register first.");
                            return true;
                        }
                        if (StringUtils.equals(instance.getAddress(), host) && port == instance.getPort()) {
                            try {
                                return scheduleListener.executeTask(key);
                            } catch (Exception e) {
                                LOG.error("Execute task throw exception.", e);
                                return false;
                            }
                        }
                        break;

                    }
                    case SCHEDULE_ALL_EXECUTE: {
                        try {
                            return scheduleListener.executeTask(key);
                        } catch (Exception e) {
                            LOG.error("Execute task throw exception. Execute this task again.", e);
                            return false;
                        }
                    }
                }
                return true;
            }
        }, retry);
    }
}
