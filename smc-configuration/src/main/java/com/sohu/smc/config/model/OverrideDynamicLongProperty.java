package com.sohu.smc.config.model;

import com.netflix.config.DynamicLongProperty;
import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/13/13
 * Time: 12:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class OverrideDynamicLongProperty extends DynamicLongProperty {
    private long propValue;

    protected OverrideDynamicLongProperty(String propName, long defaultValue) {
        super(propName, defaultValue);
        this.propValue = defaultValue;
    }

    @Override
    public long get() {
        return this.propValue;
    }

    @Override
    public Long getValue() {
        return get();
    }
}
