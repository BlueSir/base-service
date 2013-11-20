package com.sohu.smc.config.model;

import com.netflix.config.*;
import com.sohu.smc.config.service.SmcConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 11/12/13
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class AppConfiguration {
    static{
        SmcConfiguration.init();
    }

    /**
     * 从配置中心获取String类型的配置值
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicStringProperty getString(String key, String defaultValue) {
        final DynamicStringProperty property = DynamicPropertyFactory.getInstance().getStringProperty(key, defaultValue);
        return property;
    }

    /**
     * 从配置中心获取int类型的配置值
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicIntProperty getInt(String key, int defaultValue) {
        final DynamicIntProperty property = DynamicPropertyFactory.getInstance().getIntProperty(key, defaultValue);
        return property;
    }

    /**
     * 从配置中心获取long类型的配置值
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicLongProperty getLong(String key, int defaultValue) {
        final DynamicLongProperty property = DynamicPropertyFactory.getInstance().getLongProperty(key, defaultValue);
        return property;
    }

    /**
     * 从配置中心获取Boolean类型的配置值
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicBooleanProperty getBoolean(String key, boolean defaultValue) {
        final DynamicBooleanProperty property = DynamicPropertyFactory.getInstance().getBooleanProperty(key, defaultValue);
        return property;
    }
    /**
     * 从配置中心获取double类型的配置值
     *
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicDoubleProperty getDouble(String key, double defaultValue) {
        final DynamicDoubleProperty property = DynamicPropertyFactory.getInstance().getDoubleProperty(key, defaultValue);
        return property;
    }

    /**
     * 从配置中心获取String类型的配置值，并增加该配置项变更时的回调，该配置项变更时Runnable的run()会被调用
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicStringProperty getString(String key, String defaultValue, Runnable propertyChangeCallback) {
        final DynamicStringProperty property = DynamicPropertyFactory.getInstance().getStringProperty(key, defaultValue, propertyChangeCallback);
        return property;
    }

    /**
     * 从配置中心获取int类型的配置值，并增加该配置项变更时的回调，该配置项变更时Runnable的run()会被调用
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicIntProperty getInt(String key, int defaultValue, Runnable propertyChangeCallback) {
        final DynamicIntProperty property = DynamicPropertyFactory.getInstance().getIntProperty(key, defaultValue, propertyChangeCallback);
        return property;
    }

    /**
     * 从配置中心获取long类型的配置值，并增加该配置项变更时的回调，该配置项变更时Runnable的run()会被调用
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicLongProperty getLong(String key, int defaultValue, Runnable propertyChangeCallback) {
        final DynamicLongProperty property = DynamicPropertyFactory.getInstance().getLongProperty(key, defaultValue, propertyChangeCallback);
        return property;
    }

    /**
     * 从配置中心获取Boolean类型的配置值，并增加该配置项变更时的回调，该配置项变更时Runnable的run()会被调用
     * @param key　配置中心设置的Key
     * @param defaultValue 从配置中心取不到值时返回的默认值
     * @return　能动态获取最新配置值的对象
     */
    public static DynamicBooleanProperty getBoolean(String key, boolean defaultValue, Runnable propertyChangeCallback) {
        final DynamicBooleanProperty property = DynamicPropertyFactory.getInstance().getBooleanProperty(key, defaultValue, propertyChangeCallback);
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
        final DynamicDoubleProperty property = DynamicPropertyFactory.getInstance().getDoubleProperty(key, defaultValue, propertyChangeCallback);
        return property;
    }


}
