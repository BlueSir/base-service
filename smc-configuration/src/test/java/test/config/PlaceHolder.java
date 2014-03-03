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

    private Map<String,String> sysProperty;

    public Map<String, String> getSysProperty() {
        return sysProperty;
    }

    public void setSysProperty(Map<String, String> sysProperty) {
        this.sysProperty = sysProperty;
    }
}
