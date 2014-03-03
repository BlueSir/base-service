/**
 * $Id: SpyMemcachedBean.java 3552 2012-07-05 05:44:42Z nimin $
 * All Rights Reserved.
 */
package com.sohu.smc.redis;

import org.springframework.beans.factory.FactoryBean;

import java.util.Map;

public class JedisBean implements FactoryBean {

	/** 标识是否需要用双实例来实现故障转移，默认为false */
	private boolean failover;

	/** 地址列表 */
	private String masterAddress;

	/** 另一组地址列表（如果不需双实例，slave值为空） */
	private String slaveAddress;

	private String zkConn;

    private String password;

    private Map<String, Integer> dbIndexMap;

    public String getZkConn() {
        return zkConn;
    }

    public void setZkConn(String zkConn) {
        this.zkConn = zkConn;
    }

    /**
	 * @return the failover
	 */
	public boolean isFailover() {
		return failover;
	}

	/**
	 * @param failover
	 *            the failover to set
	 */
	public void setFailover(boolean failover) {
		this.failover = failover;
	}

	/**
	 * @return the masterAddress
	 */
	public String getMasterAddress() {
		return masterAddress;
	}

	/**
	 * @param masterAddress
	 *            the masterAddress to set
	 */
	public void setMasterAddress(String masterAddress) {
		this.masterAddress = masterAddress;
	}

	/**
	 * @return the slaveAddress
	 */
	public String getSlaveAddress() {
		return slaveAddress;
	}

	/**
	 * @param slaveAddress
	 *            the slaveAddress to set
	 */
	public void setSlaveAddress(String slaveAddress) {
		this.slaveAddress = slaveAddress;
	}


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Map<String, Integer> getDbIndexMap() {
        return dbIndexMap;
    }

    public void setDbIndexMap(Map<String, Integer> dbIndexMap) {
        this.dbIndexMap = dbIndexMap;
    }

	@Override
	public String toString() {
		return "JedisBean: [failover=" + failover + ", masterAddress=" + masterAddress + ", slaveAddress=" + slaveAddress + "]";
	}


    public Object getObject() throws Exception {
        return this;
    }

    @SuppressWarnings("unchecked")
    public Class getObjectType() {
        return JedisBean.class;
    }

    public boolean isSingleton() {
        return true;
    }
}
