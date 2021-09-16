package com.xxx.proj.controller;

/**
 * @author admin
 * @version 1.0.0
 * @Description TODO
 * @createTime 2021年09月05日 19:51:00
 */


import com.alibaba.dubbo.config.annotation.Reference;
import com.xxx.proj.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;

    @RequestMapping("/search")
    public Map<String, Object> search( @RequestBody Map searchMap) {
        Map<String, Object> searchResult = itemSearchService.search(searchMap);
        return searchResult;
    }
}
