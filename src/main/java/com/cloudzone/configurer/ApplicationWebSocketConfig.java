package com.cloudzone.configurer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tongqiangying@gmail.com
 * @since 2018/3/22
 */
@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class ApplicationWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {


    /**
     * websocket请求路径
     */
    private static final String websocketPaths = "/websocket";
    /**
     * 当前服务器IP
     */
    private static final String addressKey = "server.address";
    /**
     * 当前应用端口
     */
    private static final String portKey = "server.port";


    @Autowired
    private Environment environment;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        List<String> origins = new ArrayList<>();
        String address = environment.getProperty(addressKey, "127.0.0.1");
        String port = environment.getProperty(portKey, "8080");
        String origin = String.format("http://%s:%s", address, port);
        origins.add(origin);
        origins.add("*");

        // 添加了一个/websocket端点，客户端就可以通过这个端点来进行连接
        registry.addEndpoint(websocketPaths)

                //设置跨域URL
                .setAllowedOrigins(origins.toArray(new String[0]))

                // 定义请求处理器
                .addInterceptors()

                // 支持浏览器SockJS
                .withSockJS();
    }

    /**
     * 设置消息连接请求的各种规范信息
     *
     * @param config
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // 设置客户端订阅地址的前缀信息，也就是客户端接收服务端消息的地址前缀 如"/topic","/user"
        config.enableSimpleBroker("/topic");

        // 设置服务端接收地址的前缀信息，也就是客户端给服务端发消息的地址前缀 如"/app/log"
        config.setApplicationDestinationPrefixes("/app");
    }

}
