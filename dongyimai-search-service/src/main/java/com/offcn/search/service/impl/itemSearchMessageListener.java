package com.offcn.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class itemSearchMessageListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        System.out.println("接收消息");
        TextMessage textMessage = (TextMessage) message;
        try {
            List<TbItem> itemList = JSON.parseArray(textMessage.getText(), TbItem.class);
            itemSearchService.importList(itemList);
            System.out.println("导入结束！！！");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
