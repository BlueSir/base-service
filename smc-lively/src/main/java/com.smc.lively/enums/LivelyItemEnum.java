package com.smc.lively.enums;

import com.smc.lively.kafka.KafkaEnum;
import com.smc.lively.kafka.KafkaProducer;
import com.smc.lively.kafka.KafkaProducerFactory;
import com.smc.lively.service.LivelyRedis;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 2/25/14
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public enum LivelyItemEnum {

    PID_1_DAY("pid_1_day", 1, 6, "一天内的活跃用户passportId"),
    PID_3_DAY("pid_3_day", 3, 12, "三天内的活跃用户passportId"),
    CID_1_DAY("cid_1_day", 1, 6, "一天内的活跃用户cid"),
    CID_3_DAY("cid_3_day", 3, 12,"三天内的活跃用户cid"),
    CID_TEST("cid_test", 1, 1, "测试"){
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyyMMddHHmm");
        @Override
        public long getScore() {
            return Long.valueOf(sdfs.format(new Date()));
        }

        public long getMinScore(){
            return Long.valueOf(sdfs.format(DateUtils.addDays(new Date(), this.days * (-1))));
        }

        public long getMaxScore(){
            return Long.valueOf(sdfs.format(DateUtils.addHours(new Date(), 1)));
        }
        public int getLockExpire(){
            return 60;
        }
    };
    public String name;
    //数据保存的天数
    public int days;
    public String desc;
    //定时任务执行的时间间隔，以小时为单位
    public int taskPeriodWithHour;
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
    static final KafkaProducer kafkaProducer = KafkaProducerFactory.getProducer("lively");
    static final Logger LOG = LoggerFactory.getLogger(LivelyItemEnum.class);
    LivelyItemEnum(String name, int days, int taskPeriodWithHour, String desc){
        this.name = name;
        this.days = days;
        this.desc = desc;
        this.taskPeriodWithHour = taskPeriodWithHour;
    }

    public long getScore(){
        return Long.valueOf(sdf.format(new Date()));
    }

    public long getMinScore(){
        return Long.valueOf(sdf.format(DateUtils.addDays(new Date(), this.days * (-1))));
    }

    public long getMaxScore(){
        return Long.valueOf(sdf.format(DateUtils.addHours(new Date(), 1)));
    }

    public long getOverdueScore(){
        return Long.valueOf(sdf.format(DateUtils.addDays(new Date(), (this.days +1) * (-1))));
    }

    public int getLockExpire(){
        return this.taskPeriodWithHour * 60 *60;
    }

    public void deleteTask(){
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Set<String> overdue = LivelyRedis.livelyOverdue(LivelyItemEnum.this);
                Set<String> overdueMessage = new HashSet<String>();
                if(overdue != null && overdue.size() >0){
                    for(String eachItem : overdue){
                        overdueMessage.add(new StringBuilder(name).append("@").append(eachItem).toString());
                    }
                    kafkaProducer.send(KafkaEnum.LIVELY_DEL_TOPIC.name, overdueMessage);
                    LOG.info("[executeTask]:" + name + " delete overdue item success.size="+overdue.size());
                }
            }
        }, 0, this.taskPeriodWithHour, TimeUnit.HOURS);

    }

    public static LivelyItemEnum getLivelyItemByName(String name){
        LivelyItemEnum[] values = LivelyItemEnum.values();
        for(LivelyItemEnum each : values){
            if(StringUtils.equals(each.name, name)){
                return each;
            }
        }
        return null;
    }

    public static void executeTask(){
        LivelyItemEnum[] values = LivelyItemEnum.values();
        for(LivelyItemEnum each : values){
            each.deleteTask();
        }
    }

    public static void main(String[] args){
        System.out.println(LivelyItemEnum.CID_1_DAY.getMaxScore() + "|" + LivelyItemEnum.CID_1_DAY.getMinScore() +"|" + LivelyItemEnum.CID_1_DAY.getOverdueScore());

    }
}
