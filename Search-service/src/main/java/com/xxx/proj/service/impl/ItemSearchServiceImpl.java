package com.xxx.proj.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xxx.proj.pojo.TbItem;
import com.xxx.proj.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service()
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    private Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();
        Long typeid = (Long) redisTemplate.boundHashOps("itemCat").get(category);//获取模板id
        if (typeid != null) {
            //根据模板id查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeid);
            map.put("brandList", brandList);
            //根据模板id查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeid);
            map.put("specList", specList);
        }
        return map;
    }

    public Map<String, Object> search(Map searchMap) {
        System.out.println(searchMap);
        Map<String, Object> map = new HashMap<String, Object>();
        //设置高亮
        map.putAll(searchList(searchMap));
        //读取分类
        List categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);
        //查询品牌和规格列表
        String categoryName = (String) searchMap.get("category");
        if (!"".equals(categoryName)) {
            map.putAll(searchBrandAndSpecList(categoryName));
        } else if (categoryList.size() > 0) {
            map.putAll(searchBrandAndSpecList((String) categoryList.get(0)));
        }

        return map;
    }

    private Map<String, List<TbItem>> searchList(Map searchMap) {
        Map map = new HashMap();
        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮域
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);
//多关键字
        String keywords = (String) searchMap.get("keywords");
        keywords.replace(" ","");


        //查询条件
            Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
            query.addCriteria(criteria);

//过滤分类
        if (!"".equals(searchMap.get("category"))) {
            Criteria criteria1 = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(criteria1);
            query.addFilterQuery(filterQuery);
        }
//过滤品牌
        if (!"".equals(searchMap.get("brand"))) {
            Criteria criteria1 = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(criteria);
            query.addFilterQuery(filterQuery);
        }
//过滤规格
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria criteria1 = new Criteria("item_spec_" + key).is(specMap.get(key));
                System.out.println("item_spec_" + key + " : " + specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }
        }
//过滤价格
        if (!"".equals(searchMap.get("price"))) {
            String[] prices = ((String) searchMap.get("price")).split("-");
            if (!prices[0].equals("0")) {
                Criteria criteria1 = new Criteria("item_price").greaterThanEqual(prices[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }
            if (!prices[1].equals("*")) {
                Criteria criteria1 = new Criteria("item_price").lessThanEqual(prices[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }
        }


//这是分页的处理
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo == null){
            pageNo = 1;
        }
        Integer rows = (Integer) searchMap.get("pageSize");
        if (rows == null){
            rows = 20;
        }
        query.setOffset((pageNo-1)*rows);
        query.setRows(rows);


//这是排序的代码
        String sortValue =(String) searchMap.get("sort");//ASC||DESC 是可选的升序或者降序
        String sortField = (String) searchMap.get("sortField");
        if ("ASC".equals(sortValue)){
            Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
            query.addSort(sort);
        }else if ("DESC".equals(sortValue)){
            Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
            query.addSort(sort);
        }else {

        }

        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        for (HighlightEntry<TbItem> h : page.getHighlighted()) {
            TbItem entity = h.getEntity();
            if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
                entity.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        map.put("rows", page.getContent());
        map.put("totalPages",page.getTotalPages());
        map.put("total",page.getTotalElements());
        return map;
    }

    /**
     * 查询分类列表
     */
    private List searchCategoryList(Map searchMap) {
        List list = new ArrayList();
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entity : content) {
            list.add(entity.getGroupValue());//将分组结果的名称封装到返回值中
        }
        //设置分组选项
        return list;

    }
}
