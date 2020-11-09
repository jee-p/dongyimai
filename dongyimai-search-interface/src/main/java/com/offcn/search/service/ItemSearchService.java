package com.offcn.search.service;

import com.offcn.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    //搜索功能
    public Map<String,Object> search(Map searchMap);

    //导入数据
    public void importList(List<TbItem> list);

    //删除索引库数据
    public  void  dele(List goodsIdList);
}
