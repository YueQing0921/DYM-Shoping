package com.xxx.proj.service.impl;

import com.xxx.proj.pojo.TbSeller;
import com.xxx.proj.service.SellerService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailServiceImpl implements UserDetailsService {
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        TbSeller seller = sellerService.findOne(s);

        //授权
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //提交给springsecurity认证
        return new User(s, seller.getPassword(), list);

    }
}
