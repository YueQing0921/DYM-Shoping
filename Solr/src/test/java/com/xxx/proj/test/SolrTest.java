package com.xxx.proj.test;

import com.xxx.proj.util.SolrUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-*.xml")
//classpath*:applicationContext-*.xml 在所有依赖包中找名字是applicationContext-开头的xml文件
public class SolrTest {
    @Autowired
    private SolrUtil solrUtil;

    @Test
    public void testImport() {
        solrUtil.importData();
    }
    @Test
    public void testDelete() {
        solrUtil.deleteData();
    }
}
