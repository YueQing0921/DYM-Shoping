package com.xxx.proj.mapper;

import com.xxx.proj.pojo.TbBrand;
import com.xxx.proj.pojo.TbBrandExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TbBrandMapper {

    /**
     * 规格选项列表
     * @return
     */
    public List<Map> selectOptions();


    int countByExample(TbBrandExample example);

    int deleteByExample(TbBrandExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TbBrand record);

    int insertSelective(TbBrand record);

    List<TbBrand> selectByExample(TbBrandExample example);

    TbBrand selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TbBrand record, @Param("example") TbBrandExample example);

    int updateByExample(@Param("record") TbBrand record, @Param("example") TbBrandExample example);

    int updateByPrimaryKeySelective(TbBrand record);

    int updateByPrimaryKey(TbBrand record);
}