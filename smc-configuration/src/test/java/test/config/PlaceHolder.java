package test.config;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 11/15/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlaceHolder {

    private String key;
    private String value;
    private Map<String,String> sysProperty;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, String> getSysProperty() {
        return sysProperty;
    }

    public void setSysProperty(Map<String, String> sysProperty) {
        this.sysProperty = sysProperty;
    }
}
