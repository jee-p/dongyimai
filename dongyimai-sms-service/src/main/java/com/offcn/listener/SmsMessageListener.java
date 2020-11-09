package com.offcn.listener;

import com.offcn.utils.SmsUtil;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
@Component
public class SmsMessageListener implements MessageListener {
    @Autowired
    private SmsUtil smsUtil;
    @Override
    public void onMessage(Message message) {
        MapMessage mapMessage = (MapMessage) message;
        try {
            String mobile = mapMessage.getString("mobile");
            String param = mapMessage.getString("param");
            HttpResponse response = smsUtil.sendSms(mobile, param);
            System.out.println("短信发送成功："+ EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
