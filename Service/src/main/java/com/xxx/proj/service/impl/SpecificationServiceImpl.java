package com.xxx.proj.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xxx.proj.dto.PageResult;
import com.xxx.proj.mapper.TbSpecificationMapper;
import com.xxx.proj.mapper.TbSpecificationOptionMapper;
import com.xxx.proj.pojo.TbSpecification;
import com.xxx.proj.pojo.TbSpecificationExample;
import com.xxx.proj.pojo.TbSpecificationExample.Criteria;
import com.xxx.proj.pojo.TbSpecificationOption;
import com.xxx.proj.pojo.TbSpecificationOptionExample;
import com.xxx.proj.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;

    @Override
    public List<Map> selectOptions() {
        return specificationMapper.selectOptions();
    }

    /**
     * 查询全部
     */
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private TbSpecificationOptionMapper optionMapper;

    /**
     * 增加
     */
    @Override
    public void add(TbSpecification specification) {
        //主表
        specificationMapper.insert(specification);
        Long specId = specification.getId();
        //从表
        for (TbSpecificationOption option : specification.getSpecOptionList()) {
            option.setSpecId(specId);//TODO 注意这里不要写错了
            optionMapper.insert(option);
        }

    }


    /**
     * 修改
     */
    @Override
    public void update(TbSpecification specification) {
        //删除旧的options
        TbSpecificationOptionExample tbSpecificationOptionExample = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
        criteria.andSpecIdEqualTo(specification.getId());
        optionMapper.deleteByExample(tbSpecificationOptionExample);

        //修改主表
        specificationMapper.updateByPrimaryKey(specification);

        //插入新的options
        for (TbSpecificationOption option : specification.getSpecOptionList()) {
            option.setSpecId(specification.getId());//TODO 注意这里不要写错了
            optionMapper.insert(option);
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbSpecification findOne(Long id) {
        //查询主表
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        //查询从表
        TbSpecificationOptionExample tbSpecificationOptionExample = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> optionList = optionMapper.selectByExample(tbSpecificationOptionExample);

        tbSpecification.setSpecOptionList(optionList);
        return tbSpecification;

    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //删除options
            TbSpecificationOptionExample tbSpecificationOptionExample = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
            criteria.andSpecIdEqualTo(id);
            optionMapper.deleteByExample(tbSpecificationOptionExample);
            //删除主表
            specificationMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }
        }

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
