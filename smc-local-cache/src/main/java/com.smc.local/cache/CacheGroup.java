/**
 * 
 */
package com.smc.local.cache;

/**
 * @author xiaocaijing
 *
 */
public class CacheGroup {
	String group;
	long timestamp;
	
	CacheGroup(String group, long timestamp){
		this.group = group;
		this.timestamp = timestamp;
	}
	
	@Override
	public int hashCode() {
		return new Long(timestamp).hashCode()+group.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj == this) return true;
		if(obj instanceof CacheGroup){
			CacheGroup other = (CacheGroup) obj;
			if(other.group.equals(group) && other.timestamp == timestamp) return true;
		}
		return false;
	}
	
}
