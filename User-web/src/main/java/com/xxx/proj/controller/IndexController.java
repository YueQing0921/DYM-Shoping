package com.xxx.proj.controller;

/**
 * @author admin
 * @version 1.0.0
 * @Description TODO
 * @createTime 2021年09月10日 13:48:00
 */
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/index")
public class IndexController {
    @RequestMapping("/name")
    public Map showName() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map = new HashMap();
        map.put("loginName", name);
        return map;
    }
}