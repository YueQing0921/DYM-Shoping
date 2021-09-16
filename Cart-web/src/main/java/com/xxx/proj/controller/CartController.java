package com.xxx.proj.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.xxx.proj.dto.Cart;
import com.xxx.proj.dto.Result;
import com.xxx.proj.service.CartService;
import com.xxx.proj.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Reference
    private CartService cartService;

    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        String cartList_cookie_str = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        if (cartList_cookie_str == null || "".equals(cartList_cookie_str)) {
            cartList_cookie_str = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartList_cookie_str, Cart.class);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            return cartList_cookie;
        } else {
            //读取缓存中的购物车
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            //合并缓存和cookie
            cartList_redis = cartService.mergeCartList(cartList_redis, cartList_cookie);
            //将合并结果写回缓存
            cartService.saveCartListToRedis(username, cartList_redis);
            //清空cookie
            CookieUtil.deleteCookie(request, response, "cartList");
            return cartList_redis;
        }


    }

    @RequestMapping("/addItemToCartList")
    @CrossOrigin(origins = "http://localhost:9008", allowCredentials = "true")
    public Result addItemToCartList(Long itemId, Integer num) {
        try {
            List<Cart> cartList = this.findCartList();
            List<Cart> cartList_total = cartService.addItemToCart(cartList, itemId, num);
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(username)) {
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList_total), 3600 * 24, "utf-8");
            } else {
                cartService.saveCartListToRedis(username, cartList_total);
                //清空cookie
                CookieUtil.deleteCookie(request, response, "cartList");
            }
            return new Result(true, "添加购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加购物车失败");
        }
    }

    @RequestMapping("/removeAll")
    public Result removeAll() {
        try {
            CookieUtil.deleteCookie(request, response, "cartList");
            return new Result(true, "清空购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "清空购物车失败");
        }
    }
}
