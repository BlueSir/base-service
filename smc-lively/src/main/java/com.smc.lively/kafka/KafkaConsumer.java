package com.smc.lively.kafka;

import com.sohu.smc.config.model.AppConfiguration;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaConsumer {
    static final Logger LOG = LoggerFactory.getLogger(KafkaConsumer.class);
    private ConsumerConfig config;
    private String topic;
    private int partitionsNum;
    private MessageExecutor executor;
    private ConsumerConnector connector;
    private ExecutorService threadPool;
    public KafkaConsumer(String module, String topic, int partitionsNum, MessageExecutor executor) throws Exception{
        this(module, topic, partitionsNum, "lively-group", executor);
    }

    public KafkaConsumer(String module, final String topic, final int partitionsNum, final String groupId, final MessageExecutor executor) throws Exception{
        final Properties properties = new Properties();
        properties.put("zookeeper.connect", AppConfiguration.getString("smc.kafka." + module + ".zk",""));
        properties.put("zookeeper.connectiontimeout.ms", AppConfiguration.getLong("smc.kafka." + module + ".zk.timeout.ms", 10000));
        properties.put("group.id", groupId);
        properties.put("consumer.timeout.ms", AppConfiguration.getLong("smc.kafka." + module + ".consumer.timeout.ms", 5000));
        properties.put("auto.commit.enable", AppConfiguration.getBoolean("smc.kafka." + module + ".auto.commit.enable", true));
        properties.put("auto.commit.interval.ms", AppConfiguration.getLong("smc.kafka." + module + ".auto.commit.interval.ms", 10000));
        AppConfiguration.getString("smc.kafka." + module + ".consumer.version", "1.0", new Runnable() {
            @Override
            public void run() {
                LOG.info("[consumer.restart]:topic="+topic);
                close();
                init(properties, topic, partitionsNum, groupId, executor);

                try {
                    start();
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.error("[consumer.restart]:Throws exception", e);
                }
            }
        });
        init(properties, topic, partitionsNum, groupId, executor);
    }

    public void init(Properties properties, String topic, int partitionsNum, String groupId, MessageExecutor executor){
        config = new ConsumerConfig(properties);
        this.topic = topic;
        this.partitionsNum = partitionsNum;
        this.executor = executor;
    }

    public void start() throws Exception{
        connector = Consumer.createJavaConsumerConnector(config);
        Map<String,Integer> topics = new HashMap<String,Integer>();
        topics.put(topic, partitionsNum);
        Map<String, List<KafkaStream<byte[], byte[]>>> streams = connector.createMessageStreams(topics);
        List<KafkaStream<byte[], byte[]>> partitions = streams.get(topic);
        threadPool = Executors.newFixedThreadPool(partitionsNum);
        for(KafkaStream<byte[], byte[]> partition : partitions){
            threadPool.execute(new MessageRunner(partition));
        }
    }


    public void close(){
        try{
            threadPool.shutdownNow();
            LOG.info("[consumer.close]:ThreadPool shutdownNow.");
        }catch(Exception e){
            e.printStackTrace();
            LOG.error("[consumer.close]:Throws exception", e);
        }finally{
            connector.shutdown();
            LOG.info("[consumer.close]:Connector shutdown.");
        }

    }

    class MessageRunner implements Runnable{
        private KafkaStream<byte[], byte[]> partition;

        MessageRunner(KafkaStream<byte[], byte[]> partition) {
            this.partition = partition;
        }

        public void run(){
            ConsumerIterator<byte[], byte[]> it = partition.iterator();
            while(it.hasNext()){
                //connector.commitOffsets();手动提交offset,当autocommit.enable=false时使用
                MessageAndMetadata<byte[],byte[]> item = it.next();
                LOG.debug("[consumer.run]:partition=" + item.partition() + ",offset=" + item.offset());
                executor.execute(new String(item.message()));//UTF-8,注意异常
            }
        }
    }

    interface MessageExecutor {

        public void execute(String message);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        KafkaConsumer consumer = null;
        try{
            MessageExecutor executor = new MessageExecutor() {

                public void execute(String message) {
                    System.out.println("=================" + message);

                }
            };
            consumer = new KafkaConsumer("test", "test-topic", 2, executor);
            consumer.start();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
//			if(consumer != null){
//				consumer.close();
//			}
        }

    }

}
