package com.xxx.proj.service.impl;

/**
 * @author admin
 * @version 1.0.0
 * @Description TODO
 * @createTime 2021年09月09日 23:00:00
 */

import com.alibaba.dubbo.config.annotation.Service;
import com.xxx.proj.mapper.TbUserMapper;
import com.xxx.proj.pojo.TbUser;
import com.xxx.proj.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public void createSmsCode(String phone) {
        String code = (long) (Math.random() * 1000000) + "";
        //1缓存code
        redisTemplate.boundHashOps("sms").put(phone, code);

        //2发送code
        Map<String, String> map = new HashMap<>();
        map.put("mobile", phone);
        map.put("code", code);
        jmsTemplate.convertAndSend("sms", map);

    }

    @Override
    public boolean checkSmsCode(String phone, String code) {
        String smsCode = (String) redisTemplate.boundHashOps("sms").get(phone);
        if (smsCode == null) {
            return false;
        } else {
            return (smsCode.equals(code));
        }
    }



    @Autowired
    private TbUserMapper tbUserMapper;

    @Override
    public void add(TbUser user) {
        user.setCreated(new Date());
        user.setUpdated(new Date());
        //加密密码 import org.apache.commons.codec.digest.DigestUtils;
        String s = DigestUtils.md5Hex(user.getPassword());
        user.setPassword(s);
        tbUserMapper.insert(user);

    }
}
