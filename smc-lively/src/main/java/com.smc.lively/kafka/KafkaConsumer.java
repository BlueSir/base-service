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

import java.util.*;
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
    private int batchSize;
    public KafkaConsumer(String module, String topic, int partitionsNum, MessageExecutor executor) throws Exception{
        this(module, topic, partitionsNum, "lively-group", executor);
    }

    public KafkaConsumer(String module, final String topic, final int partitionsNum, final String groupId, final MessageExecutor executor) throws Exception{
        final Properties properties = new Properties();
        properties.put("zookeeper.connect", AppConfiguration.getString("smc.kafka." + module + ".consumer.zk","127.0.0.1:2181").get());
        properties.put("zookeeper.connectiontimeout.ms", AppConfiguration.getString("smc.kafka." + module + ".consumer.zk.timeout.ms", "10000").get());
        properties.put("group.id", groupId);
//        properties.put("consumer.timeout.ms", AppConfiguration.getString("smc.kafka." + module + ".consumer.timeout.ms", "10000").get());
        properties.put("auto.commit.enable", AppConfiguration.getString("smc.kafka." + module + ".consumer.auto.commit.enable", "false").get());
        properties.put("auto.commit.interval.ms", AppConfiguration.getString("smc.kafka." + module + ".consumer.auto.commit.interval.ms", "10000").get());
        final int batchSize = AppConfiguration.getInt("smc.kafka." + module + ".concumer.batch.size", 100).get(); //批量消费的大小
        AppConfiguration.getString("smc.kafka." + module + ".consumer.version", "1.0", new Runnable() {
            @Override
            public void run() {
                LOG.info("[consumer.restart]:topic="+topic);
                close();
                init(properties, topic, partitionsNum, groupId, executor, batchSize);
                try {
                    start();
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.error("[consumer.restart]:Throws exception", e);
                }
            }
        }).get();
        init(properties, topic, partitionsNum, groupId, executor, batchSize);
    }

    public void init(Properties properties, String topic, int partitionsNum, String groupId, MessageExecutor executor, int batchSize){
        config = new ConsumerConfig(properties);
        this.topic = topic;
        this.partitionsNum = partitionsNum;
        this.executor = executor;
        this.batchSize = batchSize;
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
            Set<String> messages = new HashSet<String>();
            while(it.hasNext()){
                MessageAndMetadata<byte[],byte[]> item = it.next();
                messages.add(new String(item.message()));
                if(messages.size() == batchSize){
                    System.out.println("partiton:" + item.partition() + ",offset:" + item.offset() + ",lastMessage:" + new String(item.message()));
                    executor.execute(messages);//UTF-8,注意异常
                    messages.clear();
                    connector.commitOffsets();//手动提交offset,当autocommit.enable=false时使用
                }
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        KafkaConsumer consumer = null;
        try{
            MessageExecutor executor = new MessageExecutor() {

                public void execute(Set<String> message) {
                    System.out.println("=================" + message.size());

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
