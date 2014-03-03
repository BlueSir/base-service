package com.sohu.smc.base.server.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 1/8/14
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class MD5Util {
    public static String Md5(String plainText ) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if(i<0) i+= 256;
                if(i<16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            return buf.toString();//32位的加密


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
