/** 
 * @Title: RoomList.java
 * @Description: TODO(描述)
 * @author zzy
 * @date 2022-12-31 08:45:08
 */
package com.example.websocketdemo.model;

import java.util.Map;

/**
 * @ClassName: RoomList
 * @Description: TODO(描述)
 * @author zzy
 * @date 2022-12-31 08:45:08
 */
public class RoomList implements Comparable<RoomList> {
	private Integer RoomOrderNumber;
	private Map<String, String> UserGroup;

	/**
	 * @Title: RoomList
	 * @Description: RoomList构造函数
	 * @author zzy
	 * @date 2022-12-31 10:56:58
	 */
	public RoomList() {
		super();
	}

	/**
	 * @Title: RoomList
	 * @Description: RoomList构造函数
	 * @param roomOrderNumber
	 * @param userGroup
	 * @author zzy
	 * @date 2022-12-31 10:56:52
	 */
	public RoomList(Integer roomOrderNumber) {
		super();
		RoomOrderNumber = roomOrderNumber;
	}

	/**
	 * @return roomOrderNumber
	 */
	public Integer getRoomOrderNumber() {
		return RoomOrderNumber;
	}

	/**
	 * @param roomOrderNumber 要设置的 roomOrderNumber
	 */
	public void setRoomOrderNumber(Integer roomOrderNumber) {
		RoomOrderNumber = roomOrderNumber;
	}

	/**
	 * @return userGroup
	 */
	public Map<String, String> getUserGroup() {
		return UserGroup;
	}

	/**
	 * @param userGroup 要设置的 userGroup
	 */
	public void setUserGroup(Map<String, String> userGroup) {
		UserGroup = userGroup;
	}

	/**
	 * @Title: compareTo
	 * @Description: TODO(描述)
	 * @param o
	 * @return
	 * @author zzy
	 * @date 2022-12-31 08:49:34
	 */
	@Override
	public int compareTo(RoomList o) {
		// TODO 自动生成的方法存根
		return -1;
	}

	/**
	 * @Title: equals
	 * @Description: TODO(描述)
	 * @param obj
	 * @return
	 * @author zzy
	 * @date 2022-12-31 10:51:10
	 */
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
		RoomList other = (RoomList) obj;
		if (RoomOrderNumber != other.RoomOrderNumber) {
			return false;
		}
		return true;
	}

}
