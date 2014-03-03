package com.smc.notify;

import com.nsq.BatchCallback;
import com.nsq.Message;
import com.nsq.NSQLookup;
import com.nsq.NSQProducer;
import com.nsq.exceptions.BadMessageException;
import com.nsq.exceptions.BadTopicException;
import com.nsq.exceptions.DisconnectedException;
import com.nsq.exceptions.NoConnectionsException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/23/13
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmcNSQProducer {
    private NSQProducer nsqProducer;
    private NSQLookup nsqLookup;

    public SmcNSQProducer(NSQProducer nsqProducer, NSQLookup lookup){
        this.nsqProducer = nsqProducer;
        this.nsqLookup = lookup;
    }

    public void configureBatch(String topic, BatchCallback callback, Integer maxMessages, Long maxBytes, Integer maxSeconds) {
        this.nsqProducer.configureBatch(topic, callback, maxMessages, maxBytes, maxSeconds);
    }

    /**
     * flushes all batches
     */
    public void flushBatches() {
        this.nsqProducer.flushBatches();
    }


    /**
     * produces a message in batch.  configure the batching options via
     * configureBatch method.
     *
     * @param topic
     * @param message
     */
    public void produceBatch(String topic, byte[] message) {
        this.nsqProducer.produceBatch(topic, message);
    }


    /**
     * produce multiple messages.
     *
     * @param topic
     * @param message
     * @throws com.nsq.exceptions.DisconnectedException
     * @throws com.nsq.exceptions.BadTopicException
     * @throws com.nsq.exceptions.BadMessageException
     * @throws NoConnectionsException
     */
    public void produceMulti(String topic, List<byte[]> message){
        try {
            this.nsqProducer.produceMulti(topic, message);
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

    /**
     * @param topic
     * @param message
     * @throws NoConnectionsException
     */
    public void produce(String topic, String message){
        try {
            Message msg = new Message();
            msg.setM(message);
            this.nsqProducer.produce(topic, msg.toString().getBytes());
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

    public void produce(String topic, Message message){
        try {
            this.nsqProducer.produce(topic, message.toString().getBytes());
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


    public NSQProducer getNsqProducer(){
        return this.nsqProducer;
    }

    public NSQLookup getNsqLookup(){
        return this.nsqLookup;
    }

}
