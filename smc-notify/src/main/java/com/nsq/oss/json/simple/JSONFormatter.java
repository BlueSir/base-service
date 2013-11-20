/**
 *
 */
package com.nsq.oss.json.simple;

/**
 * Register a formatter via JSONValue.registerFormatter
 * <p/>
 * <p/>
 * example:
 * <p/>
 * JSONValue.registerFormatter(Date.class, new JSONFormatter() {
 *
 * @author Dustin Norlander
 * @Override public String toJSONString(Object value) {
 * return "\"" + ((Date)value).toGMTString() + "\"";
 * }
 * });
 * @created Dec 29, 2010
 */
public interface JSONFormatter {
    public String toJSONString(Object value);
}
