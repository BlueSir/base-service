package com.smc.lively.test;

import com.smc.lively.kafka.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 3/5/14
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class KafkaTest {

    public static void main(String[] args) throws Exception {
//        KafkaProducer producer = KafkaProducerFactory.getProducer("lively");
//        for(int i=0;i<100;i++){
//            producer.send(KafkaEnum.LIVELY_ADD_TOPIC.name, "200000"+i);
//        }

        KafkaConsumer delConsumer = new KafkaConsumer("lively", KafkaEnum.LIVELY_ADD_TOPIC.name, 2, new MessageExecutor(){
            @Override
            public void execute(Set<String> message) {
                System.out.println(message);
            }
        });
        delConsumer.start();
    }
}
