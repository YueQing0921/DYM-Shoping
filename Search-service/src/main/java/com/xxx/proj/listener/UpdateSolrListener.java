package com.xxx.proj.listener;

/**
 * @author admin
 * @version 1.0.0
 * @Description TODO
 * @createTime 2021年09月09日 19:08:00
 */
import com.alibaba.fastjson.JSON;
import com.xxx.proj.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

@Component
public class UpdateSolrListener implements MessageListener {
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            List<TbItem> itemList = JSON.parseArray(textMessage.getText(), TbItem.class);
            for (TbItem item : itemList) {
                Map specMap = JSON.parseObject(item.getSpec(), Map.class);
                item.setSpecMap(specMap);
            }
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
