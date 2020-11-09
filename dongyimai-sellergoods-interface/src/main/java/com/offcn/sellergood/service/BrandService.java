package com.offcn.sellergood.service;

import com.offcn.entity.PageResult;
import com.offcn.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    public List<TbBrand> findAll();

    public PageResult findPage(int pageNum,int pageSize);

    void add(TbBrand tbBrand);

    public TbBrand fingOne(Long id);

    public void update(TbBrand tbBrand);

    public void delete(Long[] ids);
    //模糊查询
    public PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);

    //返回下拉框数据
    public List<Map> selectBrandList();
}
