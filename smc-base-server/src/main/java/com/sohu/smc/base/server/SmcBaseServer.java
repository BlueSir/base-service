package com.sohu.smc.base.server;


import com.smc.lively.enums.LivelyItemEnum;
import com.smc.notify.Notify;
import com.sohu.smc.schedule.core.util.SchedulerHolder;
import org.quartz.SchedulerException;

public class SmcBaseServer extends AbstractServer {

    public SmcBaseServer(String[] anArgs) {
        super(anArgs);
    }

    public static void main(String... anArgs) throws Exception {
        new SmcBaseServer(anArgs).run();

    }

    @Override
    public void init(Config config) {
        config.setMin_thread(128);
        config.setMax_thread(512);
        //初始化消息中心
        Notify.init(config.port);
        try {
            SchedulerHolder.scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        //活跃用户池的定时任务
        LivelyItemEnum.executeTask();

    }

}
