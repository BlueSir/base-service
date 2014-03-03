package com.nsq;

import com.smc.notify.util.JsonUtil;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/23/13
 * Time: 10:03 AM
 *
 * 通过消息中心传递的消息的实体对象
 */
public class Message {
    /**
     * type : 消息的类型
     */
    private String t;

    /**
     * host : 消息发送方或消息接收方的host
     */
    private String h;

    /**
     * port : 消息发送方或消息接收方的端口
     */
    private int p;

    /**
     * message : 消息内容　
     */
    private String m;

    public Message(){
    }

    public Message(MessageType type, String host, int port){
        this(type, host, port, null);
    }

    public Message(MessageType type, Host host){
        this(type, host, null);
    }

    public Message(MessageType type, String host, int port, String message){
        this.t = type.code;
        this.h = host;
        this.p = port;
        this.m = message;
    }

    public Message(MessageType type, Host host, String message){
        this.t = type.code;
        this.h = host.getHost();
        this.p = host.getPort();
        this.m = message;
    }
    public Message(String message){
        this.m = message;
    }

    public Message(String host, int port, String message){
        this.h = host;
        this.p = port;
        this.m = message;
    }

    public Message(Host host , String message){
        this.h = host.getHost();
        this.p = host.getPort();
        this.m = message;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    @Override
    public String toString(){
        return JsonUtil.getJsonStringFromObject(this);
    }

    public static Message convert(String json){
        if(StringUtils.isBlank(json)) return null;
        Message msg = null;
        try{
            JSONObject jsonObject = JSONObject.fromObject(json);
            msg = (Message) JSONObject.toBean(jsonObject, Message.class);
        }catch (JSONException e){
            msg = new Message();
            msg.setM(json);
        }
        return msg;
    }
}
