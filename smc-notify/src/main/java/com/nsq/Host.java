package com.nsq;


import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/23/13
 * Time: 10:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class Host {

    private String host;
    private int port;

    public Host(String host){
        this.host = host;
    }

    public Host(String host, int port){
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("");
        sb.append(StringUtils.replace(host, ".", "_"));
        sb.append("-");
        sb.append(port);
        return  sb.toString();
    }
}
