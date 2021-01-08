package com.heartsuit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
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

    // http://localhost:9000/oauth/authorize?grant_type=implicit&response_type=token&scope=pc&client_id=client0&client_secret=secret0
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client0")
                .secret(passwordEncoder().encode("secret0"))
                .authorizedGrantTypes("implicit", "refresh_token") //Implicit grant type not supported from token endpoint
                .scopes("read", "write")
                .redirectUris("http://localhost:8000/public/hello");
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
}
