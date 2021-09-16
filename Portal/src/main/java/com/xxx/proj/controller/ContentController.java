package com.xxx.proj.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xxx.proj.pojo.TbContent;
import com.xxx.proj.service.ContentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author admin
 * @version 1.0.0
 * @Description TODO
 * @createTime 2021年09月02日 09:17:00
 */
@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference(timeout=10000)
    private ContentService contentService;

    /**
     * 根据广告分类ID查询广告列表
     *
     * @param categoryId
     * @return
     */
    @RequestMapping("/findByCategoryId")
    public List<TbContent> findByCategoryId(Long categoryId) {
        return contentService.findByCategoryId(categoryId);
    }
}
