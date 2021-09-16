package com.xxx.proj.service;

import com.xxx.proj.dto.PageResult;
import com.xxx.proj.pojo.TbGoods;

import java.util.List;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface GoodsService {

    public void updateStatus(Long[] ids, String status);

    /**
     * 返回全部列表
     *
     * @return
     */
    public List<TbGoods> findAll();


    /**
     * 返回分页列表
     *
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    public void add(TbGoods goods);


    /**
     * 修改
     */
    public void update(TbGoods goods);


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    public TbGoods findOne(Long id);


    /**
     * 批量删除
     *
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 分页
     *
     * @param goods
     * @param pageNum  当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize);

}
