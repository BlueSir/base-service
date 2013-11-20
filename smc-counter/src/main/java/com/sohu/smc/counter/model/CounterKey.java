package com.sohu.smc.counter.model;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/15/13
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class CounterKey {
    private final Map<String, Object> _keys;

    public CounterKey() {
        _keys = new HashMap<String, Object>(4);
    }
    /**
     * Add the key with the given name and value to the CounterKey.
     *
     * @param name  name of the key
     * @param value value of the key
     * @return this
     */
    public CounterKey append(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("name of CounterKey part cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value of CounterKey part cannot be null");
        }
        if (value instanceof String || value instanceof Number || value instanceof Character || value instanceof Boolean) {
            _keys.put(name, value);
        }else{
            throw new IllegalArgumentException("value of CounterKey part only be simple type:[String,Number,Character,Boolean]");
        }
        return this;
    }

    @Override
    public int hashCode() {
        return _keys.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CounterKey other = (CounterKey) obj;
        if (!_keys.equals(other._keys)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        List<String> keyList = new ArrayList<String>(_keys.keySet());
        Collections.sort(keyList);

        StringBuilder b = new StringBuilder();
        for (String keyPart : keyList) {
            b.append(_keys.get(keyPart));
            b.append("_");
        }
        return b.substring(0, b.length()-1);
    }

}
