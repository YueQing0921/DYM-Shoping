package com.xxx.proj.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xxx.proj.dto.PageResult;
import com.xxx.proj.mapper.*;
import com.xxx.proj.pojo.*;
import com.xxx.proj.pojo.TbGoodsExample.Criteria;
import com.xxx.proj.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

//    @Autowired
//    private  SolrTemplate solrTemplate;

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
//            //更新solr索引
//            if ("1".equals(status)) {
//                TbItemExample tbItemExample = new TbItemExample();
//                TbItemExample.Criteria criteria = tbItemExample.createCriteria();
//                criteria.andGoodsIdEqualTo(id);
//                List<TbItem> itemList = itemMapper.selectByExample(tbItemExample);
//                for (TbItem item : itemList) {
//                    Map specMap = JSON.parseObject(item.getSpec(), Map.class);
//                    item.setSpecMap(specMap);
//                }
//                solrTemplate.saveBeans(itemList);//TODO 注意这里是带s的！！！
//                solrTemplate.commit();
//            } else {
//                Query query = new SimpleQuery("*:*");
//                org.springframework.data.solr.core.query.Criteria criteria =
//                        new org.springframework.data.solr.core.query.Criteria("item_goodsid").is(id);
//                query.addCriteria(criteria);
//
//                solrTemplate.delete(query);
//                solrTemplate.commit();
//            }

        }
    }

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbSellerMapper sellerMapper;

    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 增加
     */
    @Override
    public void add(TbGoods goods) {
        goods.setAuditStatus("0");
        //tb_goods
        goodsMapper.insert(goods);
        //tb_goods_desc
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDesc.setGoodsId(goods.getId());
        goodsDescMapper.insert(goodsDesc);
        //tb_item
        saveItemList(goods);

    }

    private void saveItemList(TbGoods goods) {
        //tb_item
        List<TbItem> itemList = goods.getItemList();
        if ("1".equals(goods.getIsEnableSpec())) {
            for (TbItem item : itemList) {
                //标题=商品名称+规格组合
                String title = goods.getGoodsName();
                Map<String, String> spec = JSON.parseObject(item.getSpec(), Map.class);
                for (String key : spec.keySet()) {
                    title += " " + spec.get(key);
                }
                item.setTitle(title);
                setItemValue(goods, item);
                itemMapper.insert(item);
            }
        } else {
            TbItem item = new TbItem();
            item.setTitle(goods.getGoodsName());
            item.setNum(9999);
            item.setIsDefault("1");
            item.setStatus("1");
            item.setPrice(goods.getPrice());
            item.setSpec("{}");
            setItemValue(goods, item);
            itemMapper.insert(item);
        }

    }

    private void setItemValue(TbGoods goods, TbItem item) {
        item.setGoodsId(goods.getId());
        item.setCategoryid(goods.getCategory3Id());
        item.setSellerId(goods.getSellerId());
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());

        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
        item.setCategory(itemCat.getName());

        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getSellerId());
        item.setSeller(seller.getName());

        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getBrandId());
        item.setBrand(brand.getName());

        String imgStr = goods.getGoodsDesc().getItemImages();
        List<Map> imgList = JSON.parseArray(imgStr, Map.class);
        if (imgList.size() > 0) {
            item.setImage(imgList.get(0).get("url") + "");
        }
    }

    /**
     * 修改
     */
    @Override
    public void update(TbGoods goods) {
        goods.setAuditStatus("0");
        //tb_goods
        goodsMapper.updateByPrimaryKey(goods);
        //tb_goods_desc
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
        //tb_item
        //删除所有旧的tbitem
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getId());
        itemMapper.deleteByExample(example);
        //添加新的tbitem
        saveItemList(goods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbGoods findOne(Long id) {
        //查询tb_goods
        TbGoods goods = goodsMapper.selectByPrimaryKey(id);
        //查询tb_goods_desc
        TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goods.getId());
        goods.setGoodsDesc(goodsDesc);
        //查询tb_item
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> itemList = itemMapper.selectByExample(tbItemExample);
        goods.setItemList(itemList);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            goodsMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /*public static void main(String[] args) {
        TbItem item = new TbItem();
        setValue(item);//对象类型引用传递
        System.out.println(item.getNum());

        int i = 5;//基本数据类型值传递
        setValeu(i);

        System.out.println(i);
    }

    private static void setValue(TbItem item) {
        item.setNum(16);
    }

    private static void setValeu(int i) {
        i = 16;
    }*/

}
