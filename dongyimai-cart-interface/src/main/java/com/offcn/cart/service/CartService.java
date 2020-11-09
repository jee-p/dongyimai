package com.offcn.cart.service;

import com.offcn.entity.Cart;

import java.util.List;

public interface CartService {
    public List<Cart> addGoodsToCart(List<Cart> cartList,Long itemId,Integer num);

    public List<Cart> findListFromRedis(String username);

    public void saveCartToRedis(String username,List<Cart> cartList);

    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
