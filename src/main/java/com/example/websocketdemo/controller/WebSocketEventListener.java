package com.example.websocketdemo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.websocketdemo.model.ChatMessage;

/**
 * 
 * @ClassName: WebSocketEventListener
 * @Description: 使用事件侦听器来侦听套接字连接和断开连接事件，以便我们可以记录这些事件并在用户加入或离开聊天室时广播它们 -
 * @author zzy
 * @date 2022-12-31 03:24:10
 */
@Component
public class WebSocketEventListener {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

	@Autowired
	private SimpMessageSendingOperations simpMessagingTemplate;

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {

		logger.info("收到一个新的网络套接字连接：");

	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String username = (String) headerAccessor.getSessionAttributes().get("username");
		if (username != null) {

			String roomid = headerAccessor.getSessionAttributes().get("roomid").toString();
			logger.info(roomid + "房间的用户断开连接 : " + username);
			// 如果断开连接的人处于等待状态，则在等待组中删除
			if (ChatController.roomUserList.containsKey(username)) {
				ChatController.roomUserList.remove(username);
			}

			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setType(ChatMessage.MessageType.LEAVE);
			chatMessage.setSender(username);
			simpMessagingTemplate.convertAndSendToUser(roomid, "/message", chatMessage);

		}
	}
}
