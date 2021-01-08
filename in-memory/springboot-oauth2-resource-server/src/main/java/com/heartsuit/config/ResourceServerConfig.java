package com.heartsuit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * @Author Heartsuit
 * @Date 2021-01-08
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        // Since we want the protected resources to be accessible in the UI as well we need session creation to be allowed (it's disabled by default in 2.0.6)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and().requestMatchers()
                .anyRequest().and().anonymous().and().authorizeRequests()
                .antMatchers("/private/read/**").access("#oauth2.hasScope('read')") //  使用client_credentials是需要去掉 and hasRole('ROLE_ADMIN') ，因为没有用户信息，也就没有对应的角色信息
                .antMatchers("/private/write/**").access("#oauth2.hasScope('write')")
                .antMatchers("/private/**").authenticated();
    }
}
