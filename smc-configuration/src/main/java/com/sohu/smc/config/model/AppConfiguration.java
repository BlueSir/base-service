package com.sohu.smc.config.model;

import com.netflix.config.*;
import com.sohu.smc.config.conf.ServerEnvEnum;
import com.sohu.smc.config.service.SmcConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 11/12/13
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class AppConfiguration {
    static final Logger LOG = LoggerFactory.getLogger(AppConfiguration.class);
    static{
        SmcConfiguration.init();
    }

    public static boolean isTestMachin(){
        return SmcConfiguration.environment != ServerEnvEnum.ONLINE && SmcConfiguration.environment != ServerEnvEnum.PRE;
    }
    /**
     * 从配置中心获取String类型的配置值
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicStringProperty getString(String key, String defaultValue) {
        return getString(key, defaultValue, null);
    }

    /**
     * 从配置中心获取int类型的配置值
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicIntProperty getInt(String key, int defaultValue) {
        return getInt(key, defaultValue, null);
    }

    /**
     * 从配置中心获取long类型的配置值
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicLongProperty getLong(String key, int defaultValue) {
        return getLong(key, defaultValue, null);
    }

    /**
     * 从配置中心获取Boolean类型的配置值
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicBooleanProperty getBoolean(String key, boolean defaultValue) {
        return getBoolean(key, defaultValue, null);
    }
    /**
     * 从配置中心获取double类型的配置值
     *
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicDoubleProperty getDouble(String key, double defaultValue) {
        return getDouble(key, defaultValue, null);
    }

    /**
     * 从配置中心获取String类型的配置值，并增加该配置项变更时的回调，该配置项变更时Runnable的run()会被调用
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicStringProperty getString(String key, String defaultValue, Runnable propertyChangeCallback) {
        DynamicStringProperty property = null;
        if(isTestMachin() && SmcConfiguration.overridePropertyMap.containsKey(key)){
            return new OverrideDynamicStringProperty(key, SmcConfiguration.overridePropertyMap.get(key));
        }
        property = DynamicPropertyFactory.getInstance().getStringProperty(key, defaultValue, propertyChangeCallback);
        LOG.info("[smc-configuration]:Get String from configuration.key="+key+", value="+property.get());
        return property;
    }

    /**
     * 从配置中心获取int类型的配置值，并增加该配置项变更时的回调，该配置项变更时Runnable的run()会被调用
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicIntProperty getInt(String key, int defaultValue, Runnable propertyChangeCallback) {
        DynamicIntProperty property = null;
        if(isTestMachin() && SmcConfiguration.overridePropertyMap.containsKey(key)){
            property = new OverrideDynamicIntProperty(key, Integer.parseInt(SmcConfiguration.overridePropertyMap.get(key)));
            return property;
        }
        property = DynamicPropertyFactory.getInstance().getIntProperty(key, defaultValue, propertyChangeCallback);
        LOG.info("[smc-configuration]:Get int from configuration.key="+key+", value="+property.get());
        return property;
    }

    /**
     * 从配置中心获取long类型的配置值，并增加该配置项变更时的回调，该配置项变更时Runnable的run()会被调用
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicLongProperty getLong(String key, int defaultValue, Runnable propertyChangeCallback) {
        DynamicLongProperty property = null;
        if(isTestMachin() && SmcConfiguration.overridePropertyMap.containsKey(key)){
            property = new OverrideDynamicLongProperty(key, Long.parseLong(SmcConfiguration.overridePropertyMap.get(key)));
            return property;
        }
        property = DynamicPropertyFactory.getInstance().getLongProperty(key, defaultValue, propertyChangeCallback);
        LOG.info("[smc-configuration]:Get long from configuration.key="+key+", value="+property.get());
        return property;
    }

    /**
     * 从配置中心获取Boolean类型的配置值，并增加该配置项变更时的回调，该配置项变更时Runnable的run()会被调用
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicBooleanProperty getBoolean(String key, boolean defaultValue, Runnable propertyChangeCallback) {
        DynamicBooleanProperty property = null;
        if(isTestMachin() && SmcConfiguration.overridePropertyMap.containsKey(key)){
            property = new OverrideDynamicBooleanProperty(key, Boolean.parseBoolean(SmcConfiguration.overridePropertyMap.get(key)));
            return property;
        }
        property = DynamicPropertyFactory.getInstance().getBooleanProperty(key, defaultValue, propertyChangeCallback);
        LOG.info("[smc-configuration]:Get boolean from configuration.key="+key+", value="+property.get());
        return property;
    }
    /**
     * 从配置中心获取double类型的配置值，并增加该配置项变更时的回调，该配置项变更时Runnable的run()会被调用
     *
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicDoubleProperty getDouble(String key, double defaultValue, Runnable propertyChangeCallback) {
        DynamicDoubleProperty property = null;
        if(isTestMachin() && SmcConfiguration.overridePropertyMap.containsKey(key)){
            property = new OverrideDynamicDoubleProperty(key, Double.parseDouble(SmcConfiguration.overridePropertyMap.get(key)));
            return property;
        }
        property = DynamicPropertyFactory.getInstance().getDoubleProperty(key, defaultValue, propertyChangeCallback);
        LOG.info("[smc-configuration]:Get double from configuration.key="+key+", value="+property.get());
        return property;
    }

}