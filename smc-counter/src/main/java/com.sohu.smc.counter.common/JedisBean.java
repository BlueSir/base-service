package com.sohu.smc.counter.common;

import org.springframework.beans.factory.FactoryBean;

/***
 *
 */
public class JedisBean implements FactoryBean {

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

	/** 标识是否需要用双实例来实现故障转移，默认为false */
	private boolean failover;

	/** 地址列表 */
	private String masterAddress;

	/** 另一组地址列表（如果不需双实例，slave值为空） */
	private String slaveAddress;

	private String zkConn;

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


    private String password;

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return this.password;
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SpyMemcachedBean [failover=" + failover + ", masterAddress=" + masterAddress + ", slaveAddress=" + slaveAddress + "]";
	}

}
