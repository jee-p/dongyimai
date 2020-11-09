package com.offcn.solr.util;

import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;
    //导入商品数据
    public void importData(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> itemList = itemMapper.selectByExample(example);
        System.out.println("导入开始！");
        for (TbItem item : itemList) {
            System.out.println(item.getTitle());
            //读取规格数据，把规格字符串转换成JSON对象
            Map<String,String> specMap = JSON.parseObject(item.getSpec(), Map.class);
            //创建一个新的map集合用于存储转换后的拼音数据
            Map<String,String> pinyin=new HashMap<String,String>();
            for (String key : specMap.keySet()) {
                pinyin.put(Pinyin.toPinyin(key,"").toLowerCase(),specMap.get(key));
            }
            item.setSpecMap(pinyin);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
        System.out.println("导入结束！");
    }
    public void deleteAll(){
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
    public static void main(String[] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-*.xml");
        SolrUtil solrUtil= (SolrUtil) context.getBean("solrUtil");
        solrUtil.importData();
    }
}
