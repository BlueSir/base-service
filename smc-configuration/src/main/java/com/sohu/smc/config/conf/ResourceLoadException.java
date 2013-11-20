/**
 * $Id$
 * All Rights Reserved.
 */
package com.sohu.smc.config.conf;

/**
 * @author <a href="mailto:minni@sohu-inc.com">NiMin</a>
 * @version 1.0 2012-7-20 下午4:53:00
 */
public class ResourceLoadException extends ServerException {
    /**  */
    private static final long serialVersionUID = 2074244612093527109L;

    public ResourceLoadException(String message) {
        super(message);
    }

    public ResourceLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
