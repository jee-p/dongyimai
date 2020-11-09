package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map<String, Object> search(Map searchMap) {
        //创建一个Map对象
        Map<String,Object> map = new HashMap();
        //高亮显示数据
        map.putAll(this.searchList(searchMap));
        //根据关键字查询到的结果获取商品分类
        List list = this.searchCategoryList(searchMap);
        map.put("categoryList",list);
        //根据模板Id查询品牌和规格数据
        String categoryName = (String) searchMap.get("category");
        if (!"".equals(categoryName)){
            map.putAll(this.searchBrandAndSpec(categoryName));
        }else{
            if (list.size()>0){
                Map brandAndSpec = this.searchBrandAndSpec((String) list.get(0));
                map.putAll(brandAndSpec);
            }
        }
        return map;
    }

    @Override
    public void importList(List<TbItem> list) {
        for (TbItem item : list) {
            Map<String,String> specMap = JSON.parseObject(item.getSpec(), Map.class);
            Map map = new HashMap();
            for (String key : specMap.keySet()) {
                map.put("item_spec_" + Pinyin.toPinyin(key,"").toLowerCase(),specMap.get(key));
            }
            item.setSpecMap(map);

        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void dele(List goodsIdList) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    //高亮显示（对查询关键字）
    private Map searchList(Map searchMap){
        Map map = new HashMap();
        //1.创建一个支持高亮的查询器对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //2.设置需要高亮的字段
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        //需要把高亮选项对象放入查询器对象
        query.setHighlightOptions(highlightOptions);
        //设置查询条件根据关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //按照分类进行过滤
        if (!"".equals(searchMap.get("category"))){
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //按照品牌进行过滤
        if (!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //按照规格进行过滤
        if (null!=searchMap.get("spec")){
           Map<String,String> specMap =  (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                String s = "item_spec_" + Pinyin.toPinyin(key,"").toLowerCase();
                System.out.println(s);
                Criteria filterCriteria = new Criteria(s).is(specMap.get(key));
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

        }
        //按照价格进行过滤
        if (!"".equals(searchMap.get("price"))){
            String[] prices = ((String) searchMap.get("price")).split("-");
            if (!prices[0].equals("0")){
                Criteria criteria1 = new Criteria("item_price").greaterThanEqual(prices[0]);
                SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(simpleFilterQuery);
            }
            if (!prices[1].equals("~")){
                Criteria criteria1 = new Criteria("item_price").lessThanEqual(prices[1]);
                SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(simpleFilterQuery);
            }
        }
        //搜索结果分页
        Integer pageNo= (Integer) searchMap.get("pageNo");
        if (pageNo==null){
            pageNo=1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize==null){
            pageSize=20;
        }
        //排序
        //接收前端传入的参数
        String sort= (String) searchMap.get("sort");
        String sortField= (String) searchMap.get("sortField");
        if (null!=sort&&!"".equals(sortField)){
            if ("ASC".equals(sort)){
                Sort sort1 = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort1);
            }
            if ("DESC".equals(sort)){
                Sort sort1 = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort1);
            }
        }
        //当前从第几条记录开始查询
        query.setOffset((pageNo-1)*pageSize);
        query.setRows(pageSize);
        //发起带高亮的查询请求
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //获取高亮集合入口
        List<HighlightEntry<TbItem>> highlightEntryList = page.getHighlighted();
        for (HighlightEntry<TbItem> highlightEntry : highlightEntryList) {
            //获取到的是已经高亮处理完成的当前对象
            TbItem tbItem = highlightEntry.getEntity();
            if (highlightEntry.getHighlights().size()>0 && highlightEntry.getHighlights().get(0).getSnipplets().size()>0){
                List<HighlightEntry.Highlight> highlightList = highlightEntry.getHighlights();
                List<String> snipplets = highlightList.get(0).getSnipplets();
                //获取第一个高亮字段对应的高亮结果，设置到商品标题上
                tbItem.setTitle(snipplets.get(0));
            }
        }
        map.put("rows",page.getContent());
        map.put("totalPages",page.getTotalPages());
        map.put("total",page.getTotalElements());
        return map;
    }

    private List searchCategoryList(Map searchMap){
        List<String> list = new ArrayList<String>();
        //创建一个查询器对象
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //发起查询
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> category = page.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = category.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            //把分组结果封装到List集合中
            list.add(entry.getGroupValue());
        }
        return list;
    }
    //查询品牌和规格数据
    private Map searchBrandAndSpec(String categoryName){
        Map map = new HashMap();
        Long typeId= (Long) redisTemplate.boundHashOps("itemCat").get(categoryName);
        if (typeId!=null){
            //根据模板id获取品牌和规格数据
            List brandList=(List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList",brandList);
            List specList=(List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList",specList);
        }
        return map;
    }
}
