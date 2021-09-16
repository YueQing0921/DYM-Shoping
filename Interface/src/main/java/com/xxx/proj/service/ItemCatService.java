package com.xxx.proj.service;

import com.xxx.proj.dto.PageResult;
import com.xxx.proj.pojo.TbItemCat;

import java.util.List;

/**
 * 商品类目服务层接口
 *
 * @author Administrator
 */
public interface ItemCatService {

    /**
     * 根据父id查询
     *
     * @param parentId
     * @return
     */
    public List<TbItemCat> findByParentId(Long parentId);

    /**
     * 返回全部列表
     *
     * @return
     */
    public List<TbItemCat> findAll();


    /**
     * 返回分页列表
     *
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    public void add(TbItemCat item_cat);


    /**
     * 修改
     */
    public void update(TbItemCat item_cat);


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    public TbItemCat findOne(Long id);


    /**
     * 批量删除
     *
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 分页
     *
     * @param item_cat
     * @param pageNum  当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(TbItemCat item_cat, int pageNum, int pageSize);

}
