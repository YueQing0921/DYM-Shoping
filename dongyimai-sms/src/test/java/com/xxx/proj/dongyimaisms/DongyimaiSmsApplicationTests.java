package com.xxx.proj.dongyimaisms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class DongyimaiSmsApplicationTests {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Test
    void contextLoads() {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", "17332336069");
        map.put("code", "1234");
        jmsTemplate.convertAndSend("dongyimai-sms", map);
    }

}

