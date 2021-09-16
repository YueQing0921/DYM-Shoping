package com.xxx.proj.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xxx.proj.dto.Cart;
import com.xxx.proj.mapper.TbItemMapper;
import com.xxx.proj.pojo.TbItem;
import com.xxx.proj.pojo.TbOrderItem;
import com.xxx.proj.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;


    @Override
    public List<Cart> addItemToCart(List<Cart> cartList, Long itemId, Integer num) {
        //读取商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //判断是否有该商品所在购物车
        Cart cart = searchCartFromCartList(cartList, item.getSellerId());
        if (cart == null) {
            //如果没有，则创建购物车，并添加商品
            TbOrderItem orderItem = this.createOrderItem(item, num);

            cart = new Cart();
            cart.setSellerId(item.getSellerId());
            cart.setSellerName(item.getSeller());
            List<TbOrderItem> orderItemList = new ArrayList<>();

            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);

        } else {
            //如果有购物车，则判断是否有商品
            TbOrderItem orderItem = this.searchOrderItemFromItemList(cart.getOrderItemList(), item.getId());

            if (orderItem == null) {
                //如果没有该商品，则添加商品
                orderItem = this.createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem);
            } else {
                //否则,合并商品数量
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(orderItem.getPrice().multiply(new BigDecimal(orderItem.getNum())));
                //判断商品数量，如小于1，则删除该商品
                if (orderItem.getNum() <= 0) {
                    cart.getOrderItemList().remove(orderItem);
                }
                //判断购物车是否清空，如果清空，则删除购物车
                if (cart.getOrderItemList().size() == 0) {
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> list1, List<Cart> list2) {
        for (Cart cart : list2) {
            for (TbOrderItem item : cart.getOrderItemList()) {
                list1 = this.addItemToCart(list1, item.getItemId(), item.getNum());
            }
        }
        return list1;
    }

    private Cart searchCartFromCartList(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    private TbOrderItem searchOrderItemFromItemList(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue()) {//TODO 注意这里比较的是itemId，不是id
                return orderItem;
            }
        }
        return null;
    }


    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setTitle(item.getTitle());
        orderItem.setNum(num);
        orderItem.setPrice(item.getPrice());
        orderItem.setPicPath(item.getImage());
        orderItem.setTotalFee(item.getPrice().multiply(new BigDecimal(num)));
        return orderItem;
    }
}
