package com.xxx.proj.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/index")
public class IndexController {

    @RequestMapping("/getName")
    public Map getName() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, String> map = new HashMap<>();
        map.put("loginName", username);
        return map;
    }
}
