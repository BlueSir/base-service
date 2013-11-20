package com.sohu.smc.config.conf;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SmcBaseConfig {

    private static PropertyBundle bundle;

    public static void setBundle(PropertyBundle propertyBundle) {
        bundle = propertyBundle;
    }

    /**
     * 获取key对应的字符串值
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        if (bundle != null) {
            return bundle.getProperty(key);
        }
        return "";
    }

    /**
     * 获取key对应的字符串值，如果为空，返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(String key, String defaultValue) {
        if (bundle != null) {
            String value = bundle.getProperty(key);
            if (StringUtils.isBlank(value)) {
                return defaultValue;
            } else {
                return value;
            }
        }
        return defaultValue;
    }

    /**
     * 获取key对应的整型值
     *
     * @param key
     * @return
     */
    public static int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * 获取key对应的整型值，如果为空，返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getInt(String key, int defaultValue) {
        if (bundle == null) return defaultValue;
        String value = bundle.getProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取bool值
     *
     * @param key
     * @return
     */
    public static Boolean getBoolean(String key) {
        if (bundle == null) return false;
        return Boolean.parseBoolean(bundle.getProperty(key));
    }

    /**
     * 把逗号分隔的值以List方式返回
     *
     * @param key
     * @return
     */
    public static List<String> getList(String key) {
        List<String> list = new ArrayList<String>();
        if (bundle == null) return list;
        String values = bundle.getProperty(key);
        if (StringUtils.isNotBlank(values)) {
            String[] items = values.split(",");
            for (String item : items) {
                list.add(item);
            }
        }
        return list;
    }

    /**
     * 把逗号分隔的值以Set方式返回，滤重
     *
     * @param key
     * @return
     */
    public static HashSet<String> getSet(String key) {
        HashSet<String> set = new HashSet<String>();
        if (bundle == null) return set;
        String values = bundle.getProperty(key);
        if (StringUtils.isNotBlank(values)) {
            String[] items = values.split(",");
            for (String item : items) {
                set.add(item);
            }

        }
        return set;
    }

}
