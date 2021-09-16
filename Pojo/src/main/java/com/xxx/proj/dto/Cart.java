package com.xxx.proj.dto;

/**
 * @author admin
 * @version 1.0.0
 * @Description TODO
 * @createTime 2021年09月10日 14:55:00
 */
import com.xxx.proj.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

public class    Cart implements Serializable {
    private String sellerId; //商户id
    private String sellerName;//商户名称
    private List<TbOrderItem> orderItemList;//SKU列表

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
