package com.xxx.proj.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author admin
 * @version 1.0.0
 * @Description TODO
 * @createTime 2021年09月01日 19:31:00
 */

@RestController
@RequestMapping("index")
public class IndexController {

    @RequestMapping("/showName")
    public Map showName() {
        //从安全框架中读取当前登录用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map = new HashMap();
        map.put("loginName", name);
        return map;
    }
}
