package com.offcn.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.offcn.pay.service.AliPayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
@Service
public class AliPayServiceImpl implements AliPayService {
    @Autowired
    private AlipayClient alipayClient;
    @Override
    public Map createNative(String out_trate_no, String total_fee) {
       Map map = new HashMap();
        //创建预下单请求接口
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        long tatal=Long.parseLong(total_fee);
        BigDecimal bigTotal = BigDecimal.valueOf(tatal);
        BigDecimal cs = BigDecimal.valueOf(100d);
        BigDecimal bigYuan = bigTotal.divide(cs);
        request.setBizContent("{"+
                " \"out_trade_no\":\""+out_trate_no+"\","+
                " \"total_amount\":\""+bigYuan.doubleValue()+"\","+
                " \"subject\":\"测试购买商品001\","+
                " \"store_id\":\"xa_001\","+
                " \"timeout_express\":\"90m\"}");
        //发起请求
        try {
            AlipayTradePrecreateResponse response = alipayClient.execute(request);
            //响应结果中获取响应码
            String code = response.getCode();
            //查看全部的响应结果
            String body = response.getBody();
            System.out.println("响应体："+body);
            if (code.equals("10000")){
                map.put("qrcode",response.getQrCode());
                map.put("out_trade_no",response.getOutTradeNo());
                map.put("total_fee",total_fee);
            }else {
                System.out.println("接口调用失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return map;
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map<String, String> map = new HashMap<>();
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{"+
                " \"out_trade_no\":\""+out_trade_no+"\","+
                " \"trade_no\":\"\"}");
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            //响应结果中获取响应码
            String code = response.getCode();

            if (code.equals("10000")){
                map.put("trade_status",response.getTradeStatus());
                map.put("out_trade_no",response.getOutTradeNo());
                map.put("trade_no",response.getTradeNo());
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return map;
    }
}
