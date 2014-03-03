package com.smc.lively.kafka;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 2/19/14
 * Time: 10:47 AM
 * To change this template use File | Settings | File Templates.
 */

import com.sohu.smc.config.model.AppConfiguration;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class KafkaProducer {
    static final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);
    private Producer<String,String> inner;
    KafkaProducer(final String module){
        Properties properties = new Properties();
        properties.put("partitioner.class", AppConfiguration.getString("smc.kafka." + module + ".partitioner.class", ""));
        properties.put("metadata.broker.list", AppConfiguration.getString("smc.kafka." + module + ".metadata.broker.list", ""));
        properties.put("producer.type", AppConfiguration.getString("smc.kafka." + module + ".producer.type", "sync"));
        properties.put("compression.codec",AppConfiguration.getInt("smc.kafka." + module + ".compression.codec", 0));
        properties.put("serializer.class", AppConfiguration.getString("smc.kafka." + module + ".serializer.class", "kafka.serializer.StringEncoder"));
        properties.put("batch.num.messages", AppConfiguration.getInt("smc.kafka." + module + ".batch.num.messages", 100));
        final ProducerConfig config = new ProducerConfig(properties);
        AppConfiguration.getString("smc.kafka." + module + ".producer.version", "1.0", new Runnable() {
            @Override
            public void run() {
                LOG.info("[producer.restart]:module="+module);
                inner = new Producer<String, String>(config);
            }
        });
        inner = new Producer<String, String>(config);
    }

    public void send(String topicName,String message) {
        if(topicName == null || message == null){
            return;
        }
        KeyedMessage<String, String> km = new KeyedMessage<String, String>(topicName,message);//如果具有多个partitions,请使用new KeyedMessage(String topicName,K key,V value).
        inner.send(km);
    }

    public void send(String topicName,Collection<String> messages) {
        if(topicName == null || messages == null){
            return;
        }
        if(messages.isEmpty()){
            return;
        }
        List<KeyedMessage<String, String>> kms = new ArrayList<KeyedMessage<String, String>>();
        for(String entry : messages){
            KeyedMessage<String, String> km = new KeyedMessage<String, String>(topicName,entry);
            kms.add(km);
        }
        inner.send(kms);
    }

    public void close(){
        inner.close();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        KafkaProducer producer = null;
        try{
            producer = new KafkaProducer("test");
            int i=50;
            while(true){
                producer.send("test-topic", "this is a sample" + i);
                System.out.println("producer: this is a sample" + i);
                i++;
                Thread.sleep(2000);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(producer != null){
                producer.close();
            }
        }

    }

}
