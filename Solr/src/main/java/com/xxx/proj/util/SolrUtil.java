package com.xxx.proj.util;

import com.alibaba.fastjson.JSON;
import com.xxx.proj.mapper.TbItemMapper;
import com.xxx.proj.pojo.TbItem;
import com.xxx.proj.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    public void importData() {
        //从数据库中读取SKU
        TbItemExample itemExample = new TbItemExample();
        TbItemExample.Criteria criteria = itemExample.createCriteria();
        criteria.andStatusEqualTo("1");//已审核
        List<TbItem> itemList = itemMapper.selectByExample(itemExample);
        for (TbItem item : itemList) {
            //TODO 注意清理数据，确保spec不为空{}
            //添加动态域
            Map specMap = JSON.parseObject(item.getSpec());
            item.setSpecMap(specMap);
            //System.out.println(item);
        }
        //将sku写入solr,注意这里使用的事saveBeans，带s！！！
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    public void deleteData() {
        //从数据库中读取SKU
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
