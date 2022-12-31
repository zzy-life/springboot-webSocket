package com.example.websocketdemo.model;

/**
 * Created by rajeevkumarsingh on 24/07/17.
 */
public class ChatMessage {
	private MessageType type;
	private String content;
	private String sender;
	private Integer roomId;

	public enum MessageType {
		CHAT, JOIN, LEAVE
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	/**
	 * @return roomId
	 */
	public Integer getRoomId() {
		return roomId;
	}

	/**
	 * @param roomId 要设置的 roomId
	 */
	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	/**
	 * @Title: toString
	 * @Description: TODO(描述)
	 * @return
	 * @author zzy
	 * @date 2022-12-31 09:08:04
	 */
	@Override
	public String toString() {
		return "ChatMessage [type=" + type + ", content=" + content + ", sender=" + sender + ", roomId=" + roomId + "]";
	}

}
