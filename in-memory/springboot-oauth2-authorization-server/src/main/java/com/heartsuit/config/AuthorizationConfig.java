package com.heartsuit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

/**
 * @Author Heartsuit
 * @Date 2021-01-08
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {
    /**
     * Springboot2.x需要配置密码加密，否则报错：Encoded password does not look like BCrypt
     *
     * @return
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * password 密码模式需要在认证服务器中设置 中配置AuthenticationManager
     * 否则报错：Unsupported grant type: password
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    // http://localhost:9000/oauth/token?username=admin&password=123456&grant_type=password&scope=read%20write&client_id=client1&client_secret=secret1
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client1")
                .secret(passwordEncoder().encode("secret1"))
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("read", "write");
    }

    /**
     * [{"timestamp":"2021-01-08T05:56:40.950+0000","status":403,"error":"Forbidden","message":"Forbidden","path":"/oauth/check_token"}]
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.authenticationManager(authenticationManager);
    }
}
