package com.sohu.smc.config.model;

import com.netflix.config.DynamicDoubleProperty;
import com.netflix.config.DynamicStringProperty;
import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/13/13
 * Time: 12:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class OverrideDynamicDoubleProperty extends DynamicDoubleProperty {
    private double propValue;

    protected OverrideDynamicDoubleProperty(String propName, double defaultValue) {
        super(propName, defaultValue);
        this.propValue = defaultValue;
    }

    @Override
    public double get() {

        return propValue;
    }

    @Override
    public Double getValue() {
        return get();
    }
}
