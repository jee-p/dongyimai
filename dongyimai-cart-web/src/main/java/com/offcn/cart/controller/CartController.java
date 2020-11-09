package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Cart;
import com.offcn.entity.Result;
import com.offcn.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.geom.RectangularShape;
import java.util.List;

@RestController
@RequestMapping("cart")
public class CartController {
    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartListString==null||cartListString.equals("")){
            cartListString="[]";
        }
        List<Cart> cartListCookie = JSON.parseArray(cartListString, Cart.class);
        if (name.equals("anonymousUser")){
            return cartListCookie;
        }else {
            List<Cart> cartListRedis = cartService.findListFromRedis(name);
            //如果存在cookie购物车
            if (cartListCookie.size()>0){
                //合并购物车
                cartListRedis = cartService.mergeCartList(cartListRedis, cartListCookie);
                //清楚cookie
                CookieUtil.deleteCookie(request,response,"cartList");
                //更新缓存
                cartService.saveCartToRedis(name,cartListRedis);
            }
            return cartListRedis;
        }

    }
    @RequestMapping("/addToCart")
    public Result addToCart(Long itemId,Integer num){
        response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials","true");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            List<Cart> cartList = this.findCartList();
            cartList = cartService.addGoodsToCart(cartList,itemId,num);
            if (username.equals("anonymousUser")){
                CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"UTF-8");
            }else {
                cartService.saveCartToRedis(username,cartList);
            }

            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }
}
