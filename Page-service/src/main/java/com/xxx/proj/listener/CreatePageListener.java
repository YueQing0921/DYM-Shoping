package com.xxx.proj.listener;

/**
 * @author admin
 * @version 1.0.0
 * @Description TODO
 * @createTime 2021年09月09日 19:21:00
 */
import com.xxx.proj.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class CreatePageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long[] ids = (Long[]) objectMessage.getObject();
            for (Long id : ids) {
                itemPageService.createHtml(id);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
