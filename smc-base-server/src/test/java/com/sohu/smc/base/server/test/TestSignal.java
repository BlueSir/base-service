package com.sohu.smc.base.server.test;

import com.sohu.smc.config.service.SmcConfiguration;
import org.apache.commons.lang.StringUtils;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("restriction")
public class TestSignal implements SignalHandler {

    private void signalCallback(Signal sn) {
        System.out.println(sn.getName() + "is recevied.");
    }

    @Override
    public void handle(Signal signalName) {
        signalCallback(signalName);
    }

    public static void main(String[] args) throws InterruptedException {
//        request.setCharacterEncoding("utf-8");
//        String auth = (String)request.getSession().getAttribute("auth");
//        response.setCharacterEncoding("utf-8");
//        String act = request.getParameter("act");
//        String key = request.getParameter("key");
//        String message = request.getParameter("message");
//        String currKey = request.getParameter("prefix");
//        if(StringUtils.equals(act, "del")){
//            try{
//                boolean isSucc = SmcConfiguration.remove(key);
//                if(isSucc) message = "删除成功！key=" + key;
//                else message = "删除失败，请稍等再试！" + key;
//            }catch(Exception e){
//                message = e.getMessage();
//            }
//        }
//        if(StringUtils.isNotBlank(act)){
//            message = URLEncoder.encode(message, "UTF-8");
//            response.sendRedirect("config.jsp?message="+message);
//        }
//
//        if(message == null) message = "";
//        Map<String,Map<String, Map<String, Object>>> properties = null;
//        try{
//            properties = SmcConfiguration.properties();
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//        if(properties != null){
//            Iterator<String> prefixIt = properties.keySet().iterator();
//            int counter = 0;
//            while(prefixIt.hasNext()){
//                String prefix = prefixIt.next();
//                counter ++;
//                if(counter == 1){
//                    if(StringUtils.isBlank(currKey)){
//                        currKey = prefix;
//                    }
//                }
//                if(StringUtils.equals(currKey,prefix)) out.print("class=\"active\"");
//            }
//        }
//
//                Map<String, Map<String, Object>> values = properties.get(currKey);
//                if(values != null){
//                    Iterator<String> valuesIt = values.keySet().iterator();
//                    while(valuesIt.hasNext()){
//                        String subPrefix = valuesIt.next();
//                        Map<String,Object> subProperties = values.get(subPrefix);
//                        Iterator<String> propIt = subProperties.keySet().iterator();
//                        while(propIt.hasNext()){
//                            String eachKey = propIt.next();
//                            Object eachValue = subProperties.get(eachKey);
//                            String modifyScript = "<a href=\"modifyConfig.jsp?act=show&key=" + eachKey +"&value=" + eachValue +"\">修改</a>";
//                            String delScript = "<a href=\"javascript:deleteConfirm('"+eachKey+"');\">删除</a>";
//                        }
//                    }
//                }



                        }

}