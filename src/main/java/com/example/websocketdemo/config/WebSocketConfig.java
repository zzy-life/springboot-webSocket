package com.example.websocketdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * 
 * @ClassName: WebSocketConfig
 * @Description: 配置 websocket 端点和消息代理
 * @author zzy
 * @date 2022-12-31 03:17:08
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	/**
	 * 
	 * @Title: registerStompEndpoints
	 * @Description: 在第一种方法中，我们注册了一个 websocket 端点，客户端将使用该端点连接到我们的 websocket 服务器。
	 *               注意withSockJS()与端点配置一起使用。SockJS用于为不支持 websocket 的浏览器启用回退选项。
	 * @param registry
	 * @author zzy
	 * @date 2022-12-31 03:17:59
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {

		registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
	}

	/**
	 * 
	 * @Title: configureMessageBroker
	 * @Description: 我们正在配置一个消息代理，用于将消息从一个客户端路由到另一个客户端。
	 * 
	 *               第一行定义目的地以“/app”开头的消息应该被路由到消息处理方法。
	 * 
	 *               并且，第二行定义目的地以“/topic”开头的消息应该路由到消息代理。消息代理向订阅特定主题的所有已连接客户端广播消息。
	 * @param registry
	 * @author zzy
	 * @date 2022-12-31 03:20:46
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app");
		registry.enableSimpleBroker("/topic", "/user"); // Enables a simple in-memory broker

		// Use this for enabling a Full featured broker like RabbitMQ

		/*
		 * registry.enableStompBrokerRelay("/topic") .setRelayHost("localhost")
		 * .setRelayPort(61613) .setClientLogin("guest") .setClientPasscode("guest");
		 */
	}

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
		registration.setSendTimeLimit(15 * 1000).setSendBufferSizeLimit(512 * 1024);
		registration.setMessageSizeLimit(128 * 1024);
	}

}
