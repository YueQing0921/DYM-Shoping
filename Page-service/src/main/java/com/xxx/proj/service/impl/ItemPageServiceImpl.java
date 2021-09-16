package com.xxx.proj.service.impl;

import com.xxx.proj.mapper.TbGoodsDescMapper;
import com.xxx.proj.mapper.TbGoodsMapper;
import com.xxx.proj.mapper.TbItemCatMapper;
import com.xxx.proj.mapper.TbItemMapper;
import com.xxx.proj.pojo.TbGoods;
import com.xxx.proj.pojo.TbGoodsDesc;
import com.xxx.proj.pojo.TbItem;
import com.xxx.proj.pojo.TbItemExample;
import com.xxx.proj.service.ItemPageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Value("${freemarker.path}")
    private String path;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean deleteHtml(Long goodsId) {
        File file = new File(path + goodsId + ".html");
        return file.delete();
    }

    @Override
    public boolean createHtml(Long goodsId) {
        Configuration configuration = freeMarkerConfig.getConfiguration();
        FileOutputStream fileOutputStream = null;
        try {
            //获取模版类
            Template template = configuration.getTemplate("item.ftl");

            //读取数据
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
            TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            tbGoods.setGoodsDesc(tbGoodsDesc);

            String category1Name = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String category2Name = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String category3Name = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();

            TbItemExample tbItemExample = new TbItemExample();
            TbItemExample.Criteria criteria = tbItemExample.createCriteria();
            criteria.andGoodsIdEqualTo(tbGoods.getId());
            List<TbItem> itemList = itemMapper.selectByExample(tbItemExample);
            tbItemExample.setOrderByClause("is_default desc");
            tbGoods.setItemList(itemList);


            Map map = new HashMap<>();
            map.put("goods", tbGoods);
            map.put("category1Name", category1Name);
            map.put("category2Name", category2Name);
            map.put("category3Name", category3Name);

            //e:/html/12341234.html
            fileOutputStream = new FileOutputStream(new File(path + goodsId + ".html"));
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");
            template.process(map, outputStreamWriter);
            return true;

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
