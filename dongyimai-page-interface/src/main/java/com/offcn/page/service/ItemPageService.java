package com.offcn.page.service;

public interface ItemPageService {
    //生成商品详情页
    public boolean getHtml(Long goodsId);
    //删除静态页面
    public boolean deleteHtml(Long[] ids);
}
