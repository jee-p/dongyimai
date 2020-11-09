package com.offcn.shop.service;

import com.offcn.pojo.TbSeller;
import com.offcn.sellergood.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //创建一个权限集合
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        list.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        //获取商家对象
        TbSeller seller = sellerService.findOne(username);
        if(seller!=null){

            if(seller.getStatus().equals("1")){
                return new User(username,seller.getPassword(),list);
            }else {
                return null;
            }

        }
        return null;
    }
}
