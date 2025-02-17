//package com.sugarcanelabour.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
//
//public class RawWebSocketConfig implements WebSocketConfigurer{
//
//	  private static final Logger logger = LoggerFactory.getLogger(RawWebSocketConfig.class);
//
//	    @Override
//	    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//	        registry.addHandler(new TextWebSocketHandler() {
//	            @Override
//	            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//	                logger.info("Raw WebSocket connection established with session ID: " + session.getId());
//	                session.sendMessage(new org.springframework.web.socket.TextMessage("Welcome to the raw WebSocket!"));
//	            }
//
//	            @Override
//	            public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
//	                logger.info("Raw WebSocket connection closed with session ID: " + session.getId());
//	            }
//
//	            @Override
//	            protected void handleTextMessage(WebSocketSession session, org.springframework.web.socket.TextMessage message) throws Exception {
//	                logger.info("Raw WebSocket message received: " + message.getPayload());
//	                session.sendMessage(new org.springframework.web.socket.TextMessage("Echo: " + message.getPayload()));
//	            }
//	        }, "/cri/ws").setAllowedOriginPatterns("*");
//	    }
//}
