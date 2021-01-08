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
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

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

    @Autowired
    private DataSource dataSource;

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    public ClientDetailsService clientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    }

    /**
     * password 密码模式需要在认证服务器中设置 中配置AuthenticationManager 否则报错：Unsupported grant
     * type: password
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    // http://localhost:9000/oauth/authorize?grant_type=implicit&response_type=token&scope=read20%write&client_id=client0&client_secret=secret0
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.inMemory()
//                .withClient("client0")
//                .secret(passwordEncoder().encode("secret0"))
//                .authorizedGrantTypes("implicit", "refresh_token") //Implicit grant type not supported from token endpoint
//                .scopes("read", "write")
//                .redirectUris("http://localhost:8000/public/hello");
        /**
         * 必须将secret加密后存入数据库，否则报错：Encoded password does not look like BCrypt
         */
        // System.out.println("OK:" + new BCryptPasswordEncoder().encode("secret"));
        clients.withClientDetails(clientDetailsService());
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
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore());
        endpoints.authenticationManager(authenticationManager);
        // DefaultTokenServices tokenServices = new DefaultTokenServices();
        // tokenServices.setTokenStore(endpoints.getTokenStore());
        // tokenServices.setSupportRefreshToken(true);
        // tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        // tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
        // tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30)); // 30天
        // endpoints.tokenServices(tokenServices);
    }
}
