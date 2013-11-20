/**
 * $Id$
 * All Rights Reserved.
 */
package com.sohu.smc.config.conf;

/**
 * @author <a href="mailto:minni@sohu-inc.com">NiMin</a>
 * @version 1.0 2012-7-20 下午4:44:24
 */
public class ServerException extends RuntimeException {

    /**  */
    private static final long serialVersionUID = 4204249425587512211L;

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
