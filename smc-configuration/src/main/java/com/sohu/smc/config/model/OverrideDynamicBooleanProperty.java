package com.sohu.smc.config.model;

import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicStringProperty;
import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/13/13
 * Time: 12:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class OverrideDynamicBooleanProperty extends DynamicBooleanProperty {
    private boolean propValue;

    protected OverrideDynamicBooleanProperty(String propName, boolean defaultValue) {
        super(propName, defaultValue);
        this.propValue = defaultValue;
    }

    @Override
    public boolean get() {
        return this.propValue;
    }

    @Override
    public Boolean getValue() {
        return get();
    }

}
