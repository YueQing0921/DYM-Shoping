package com.xxx.proj.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xxx.proj.dto.PageResult;
import com.xxx.proj.mapper.TbSpecificationOptionMapper;
import com.xxx.proj.mapper.TbTypeTemplateMapper;
import com.xxx.proj.pojo.TbSpecificationOption;
import com.xxx.proj.pojo.TbSpecificationOptionExample;
import com.xxx.proj.pojo.TbTypeTemplate;
import com.xxx.proj.pojo.TbTypeTemplateExample;
import com.xxx.proj.pojo.TbTypeTemplateExample.Criteria;
import com.xxx.proj.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Override
    public List<Map> selectSpecList(Long typeId) {
        //读取模版对象
        TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(typeId);
        //读取规格列表
        List<Map> specList = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
        //读取规格选项列表
        for (Map spec : specList) {
            TbSpecificationOptionExample tbSpecificationOptionExample = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
            criteria.andSpecIdEqualTo(Long.valueOf(spec.get("id") + ""));
            List<TbSpecificationOption> optionList = specificationOptionMapper.selectByExample(tbSpecificationOptionExample);
            spec.put("options", optionList);
        }
        return specList;
    }

    @Override
    public List<Map> selectOptions() {
        return typeTemplateMapper.selectOptions();
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSpecificationOptionMapper optionMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbTypeTemplate> findAll() {

        List<TbTypeTemplate> typeTemplateList = typeTemplateMapper.selectByExample(null);


        for (TbTypeTemplate typeTemplate : typeTemplateList) {
            //品牌
            List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandList);

            //规格
            List<Map> specList = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
            for (Map spec : specList) {
                TbSpecificationOptionExample tbSpecificationOptionExample = new TbSpecificationOptionExample();
                TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
                criteria.andSpecIdEqualTo(Long.valueOf(spec.get("id") + ""));
                List<TbSpecificationOption> optionList = optionMapper.selectByExample(tbSpecificationOptionExample);
                spec.put("options", optionList);
            }
            redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specList);
        }
        return typeTemplateList;
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }
        }

        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
