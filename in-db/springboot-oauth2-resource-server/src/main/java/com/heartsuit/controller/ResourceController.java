package com.heartsuit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Heartsuit
 * @Date 2021-01-08
 */
@RestController
public class ResourceController {

    @GetMapping("/public/{info}")
    public String publicResource(@PathVariable String info) {
        return "Public information : " + info;
    }

    @GetMapping("/private/{info}")
    public String privateResource(@PathVariable String info) {
        return "Private information : " + info;
    }
    @GetMapping("/private/read/{info}")
    public String privateRead(@PathVariable String info) {
        return "Private read : " + info;
    }

    @GetMapping("/private/write/{info}")
    public String privateWrite(@PathVariable String info) {
        return "Private write : " + info;
    }

}
