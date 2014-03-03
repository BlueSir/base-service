package com.sohu.smc.base.server.modle;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 1/8/14
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class User {
    private String userName;
    private String password;
    private String name;
    private boolean verify = false;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVerify() {
        return verify;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }
}
