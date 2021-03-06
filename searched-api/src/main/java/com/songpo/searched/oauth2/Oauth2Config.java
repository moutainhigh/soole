package com.songpo.searched.oauth2;

import com.songpo.searched.redis.RedisTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * @author SongpoLiu
 */
@Configuration
public class Oauth2Config {

    @Bean
    public RedisTokenStore tokenStore(RedisConnectionFactory connectionFactory) {
        return new RedisTokenStore(connectionFactory);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @EnableResourceServer
    protected static class ResourcesServer extends ResourceServerConfigurerAdapter {

        @Autowired
        private RedisTokenStore tokenStore;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.tokenStore(tokenStore);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .headers().frameOptions().sameOrigin().and()
                    .authorizeRequests()
                    // 不拦截获取token的请求
                    .antMatchers(HttpMethod.OPTIONS, "/oauth/token").permitAll()
                    // 不拦截授权接口
                    .antMatchers("/oauth/**").permitAll()
                    // 不拦截SwaggerUi
                    .antMatchers("/swagger-ui.html", "/swagger-resources/**", "/v2/**", "/webjars/**", "/doc.html").permitAll()
                    // 不拦截登录接口
                    .antMatchers("/api/common/v1/system/login").permitAll()
                    // 不拦截注册接口
                    .antMatchers("/api/common/v1/system/register").permitAll()
                    // 不拦截短信接口
                    .antMatchers("/api/common/v1/sms/verify-code").permitAll()
                    // 不拦截忘记密码接口
                    .antMatchers("/api/common/v1/system/forgot-password").permitAll()
                    // 开放所有接口
                    .antMatchers("/api/common/**").permitAll()
                    .antMatchers("/api/base/**").permitAll()
                    // 其他请求都需要经过授权
                    .anyRequest().authenticated();
        }
    }

    @EnableAuthorizationServer
    protected static class AuthorizationServer extends AuthorizationServerConfigurerAdapter {
        @Autowired
        private RedisTokenStore tokenStore;

        @Autowired
        private MyClientDetailsServiceImpl clientDetailsService;

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
            oauthServer.checkTokenAccess("permitAll()");
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.withClientDetails(clientDetailsService);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
            endpoints.tokenStore(tokenStore);
        }

    }
}