package com.songpo.searched.config;

import com.songpo.searched.redis.MyKeyExpirationEventMessageListener;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * @author 刘松坡
 */
@Configuration
public class BaseConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    MyKeyExpirationEventMessageListener messageListener(RedisMessageListenerContainer container) {
        return new MyKeyExpirationEventMessageListener(container);
    }

    @Bean
    RedisMessageListenerContainer keyExpirationListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);
        listenerContainer.addMessageListener(messageListener(listenerContainer), Collections.singleton(new PatternTopic("__keyevent@*__:expired")));
        return listenerContainer;
    }

}
