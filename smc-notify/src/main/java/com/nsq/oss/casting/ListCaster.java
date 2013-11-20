/**
 *
 */
package com.nsq.oss.casting;

import com.nsq.oss.TypeCast;

import java.util.Collection;


/**
 * @author Dustin Norlander
 * @created Dec 1, 2010
 */
public class ListCaster extends TypeCaster<Collection> {
    /* (non-Javadoc)
     * @see com.nsq.oss.casting.TypeCaster#doCast(java.lang.Class, java.lang.Object)
     */
    @Override
    protected Collection doCast(Class cls, Object obj) {
        return TypeCast.toList(obj);
    }
}
