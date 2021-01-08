package com.heartsuit.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Heartsuit
 * @Date 2021-01-08
 */
@RestController
public class LoginController {
    @Value("${server.port}")
    private String port;

    @GetMapping("/login")
    public String serve() {
        return "This is Server: " + port;
    }
}
