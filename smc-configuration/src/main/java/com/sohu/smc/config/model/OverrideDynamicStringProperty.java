package com.sohu.smc.config.model;

import com.netflix.config.DynamicStringProperty;
import com.netflix.config.PropertyWrapper;
import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/13/13
 * Time: 12:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class OverrideDynamicStringProperty extends DynamicStringProperty {
    private String propValue;

    protected OverrideDynamicStringProperty(String propName, String defaultValue) {
        super(propName, defaultValue);
        this.propValue = defaultValue;
    }

    @Override
    public String get() {
        return propValue;
    }

    @Override
    public String getValue() {
        return get();
    }
}
