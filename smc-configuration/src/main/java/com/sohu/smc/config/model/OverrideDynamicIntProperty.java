package com.sohu.smc.config.model;

import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicStringProperty;
import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/13/13
 * Time: 12:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class OverrideDynamicIntProperty extends DynamicIntProperty {
    private int propValue;

    protected OverrideDynamicIntProperty(String propName, int propValue) {
        super(propName, propValue);
        this.propValue = propValue;
    }

    @Override
    public int get() {
        return this.propValue;
    }

    @Override
    public Integer getValue() {
        return get();
    }
}
