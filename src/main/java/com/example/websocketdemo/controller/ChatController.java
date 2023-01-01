package com.example.websocketdemo.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.websocketdemo.model.ChatMessage;
import com.example.websocketdemo.model.ChatMessage.MessageType;
import com.example.websocketdemo.model.RoomList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 
 * @ClassName: ChatController
 * @Description: 我们将在控制器中定义消息处理方法。这些方法将负责从一个客户端接收消息，然后将其广播给其他客户端
 * @author zzy
 * @date 2022-12-31 03:21:53
 */
@Controller
public class ChatController {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	/**
	 * 正在等待的人
	 */
	public static ConcurrentHashMap<String, String> roomUserList = new ConcurrentHashMap<String, String>();

	/**
	 * 房间
	 */
	private RoomList room = new RoomList();
	/**
	 * 一个房间能有几个人
	 */
	private final static Integer Number_Rooms = 3;

	/**
	 * 
	 * @Title: sendMessage
	 * @Description: 广播给全体人员
	 *               <p>
	 *               ("/topic/public")是发布路径，前端可以订阅
	 *               </p>
	 * @param
	 * @return ChatMessage
	 * @author zzy
	 * @date 2022-12-31 03:27:33
	 */
	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/public")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
		System.out.println("ChatController.sendMessage()");
		return chatMessage;
	}

	/**
	 * 
	 * @Title: sendMessage
	 * @Description: 新用户加入
	 *               <p>
	 *               ("/topic/public")是订阅路径
	 *               </p>
	 * @param
	 * @return ChatMessage
	 * @author zzy
	 * @throws JsonProcessingException
	 * @date 2022-12-31 03:27:33
	 */
	@MessageMapping("/chat.addUser")
	public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor)
			throws JsonProcessingException {
		// 如果新的用户和等待组重名,推送给新的用户
		if (roomUserList.containsKey(chatMessage.getSender())) {
			chatMessage.setType(MessageType.ERROR);
			simpMessagingTemplate.convertAndSendToUser(chatMessage.getSender(), "/room", chatMessage);
			return chatMessage;
		}
		// Add username in web socket session
		System.out.println("用户名:" + chatMessage.getSender());

		System.out.println("SessionId:" + headerAccessor.getSessionId());

		handlePrivateDouser(chatMessage, headerAccessor);
		headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		headerAccessor.getSessionAttributes().put("roomid", chatMessage.getRoomId());

		return chatMessage;
	}

	/**
	 * 队列处理，如果队列达到3个人则 给同一个房间中的所有人发送消息
	 * 
	 * @throws JsonProcessingException
	 * 
	 */

	public void handlePrivateDouser(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor)
			throws JsonProcessingException {
		// 队列没人
		if (roomUserList.size() == 0) {
			// 生成房间号给小房间对象
			room.setRoomOrderNumber((int) (0 + Math.random() * (999 - 0 + 1)));

		}
		// 等待组加入一个人
		roomUserList.put(chatMessage.getSender(), headerAccessor.getSessionId());
		// 给每个用户加入房间号
		chatMessage.setRoomId(room.getRoomOrderNumber());
		// 把等待组信息塞入到消息
		Map<String, Object> contentMap = new HashMap<String, Object>();
		contentMap.put("length", roomUserList.size());
		contentMap.put("users", roomUserList);
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		chatMessage.setContent(mapper.writeValueAsString(contentMap));
		// 给map（房间）里的所有key（用户名）发送消息；在前端每个用户都订阅了自己用户名的频道
		Set<String> keys = roomUserList.keySet();
		for (String v : keys) {
			simpMessagingTemplate.convertAndSendToUser(v, "/room", chatMessage);
		}
		// 如果等待组已满，则给小房间对象加入用户组
		if (roomUserList.size() == Number_Rooms) {
			room.setUserGroup(roomUserList);

			// 等待组清空
			roomUserList.clear();

		}

	}

	/**
	 * 给指定房间发送WebSocket消息
	 * 
	 */

	@MessageMapping("/chat.user")
	public void handlePrivateChat(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor)
			throws Exception {

		if (message.getRoomId() != null) {

			simpMessagingTemplate.convertAndSendToUser(message.getRoomId().toString(), "/message", message);
		}

	}

}
