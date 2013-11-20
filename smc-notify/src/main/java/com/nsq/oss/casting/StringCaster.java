/**
 *
 */
package com.nsq.oss.casting;

import com.nsq.oss.IsoDateUtil;

import java.util.Date;


/**
 * String caster.
 * <p/>
 * will translate to a string.  Strings are trimed of whitespace, empty string is considered null.
 *
 * @author Dustin Norlander
 * @created Nov 30, 2010
 */
public class StringCaster extends TypeCaster<String> {

    /* (non-Javadoc)
     * @see com.nsq.oss.casting.TypeCaster#cast(java.lang.Class, java.lang.Object)
     */
    @Override
    protected String doCast(Class cls, Object obj) {
        Object val = obj;

        //check if its a string array
        //we need to do this because all values from a
        //Request.getParameterMap are of type string[]
        if (obj instanceof String[]) {
            String[] array = ((String[]) obj);
            if (array.length == 1)
                return array[0];
            return null;
        } else if (obj instanceof byte[]) {
            try {
                val = new String((byte[]) obj, "UTF-8");
            } catch (Exception x) {

            }
        } else if (obj instanceof Date) {
            return IsoDateUtil.getUTCTimestamp((Date) obj);
        }

        String str = val.toString().trim();
        if (str.isEmpty())
            return null;
        return str;
    }
}
