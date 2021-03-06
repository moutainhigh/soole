//package com.songpo.searched.websocket;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.com.songpo.searched.alipay.configProperties.MessageBrokerRegistry;
//import org.springframework.web.socket.com.songpo.searched.alipay.configProperties.annotation.*;
//import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
//import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
//
///**
// * @author 刘松坡
// */
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig extends WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry
//                .addEndpoint("/sp_ws")
//                .setAllowedOrigins("*")
//                .setHandshakeHandler(new DefaultHandshakeHandler(new TomcatRequestUpgradeStrategy()))
//                .withSockJS()
//                .setClientLibraryUrl("//cdn.jsdelivr.net/sockjs/1.1.4/sockjs.min.js");
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableSimpleBroker("/topic", "/queue");
//        registry.setApplicationDestinationPrefixes("/app");
//    }
//
//    @Bean
//    public MyWebSocketHandlerDecoratorFactory decoratorFactory() {
//        return new MyWebSocketHandlerDecoratorFactory();
//    }
//
//    @Override
//    public void configureWebSocketTransport(final WebSocketTransportRegistration registration) {
//        registration.addDecoratorFactory(decoratorFactory());
//        super.configureWebSocketTransport(registration);
//    }
//}
