package com.xxx.proj.service;

import com.xxx.proj.dto.Cart;

import java.util.List;

public interface CartService {

    /**
     * 添加商品到购物车列表
     *
     * @param cartList 购物车列表
     * @param itemId   商品id（skuId）
     * @param num      商品数量
     */
    public List<Cart> addItemToCart(List<Cart> cartList, Long itemId, Integer num);

    public List<Cart> findCartListFromRedis(String username);

    public void saveCartListToRedis(String username, List<Cart> cartList);

    public List<Cart> mergeCartList(List<Cart> list1, List<Cart> list2);
}
