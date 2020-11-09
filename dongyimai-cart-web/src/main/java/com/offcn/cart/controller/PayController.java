package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.offcn.entity.Result;
import com.offcn.order.service.OrderService;
import com.offcn.pay.service.AliPayService;
import com.offcn.pojo.TbPayLog;
import com.offcn.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
@EnableDubbo
@RestController
@RequestMapping("/pay")
public class PayController {
   @Reference(lazy = true,check = false)
   private AliPayService aliPayService;

   @Reference(lazy = true,check = false)
   private OrderService orderService;
    @RequestMapping("/createNative")
    public Map createNative(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.searchLogByUserId(userId);
        if (null!=payLog){
            return aliPayService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");
        }else {
            return new HashMap();
        }
    }

    //查询支付状态
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result = null;
        int x=0;
        while (true){
            //调用查询接口
            Map<String,String> map=null;
           map = aliPayService.queryPayStatus(out_trade_no);
           if (map==null){
               result=new Result(false,"支付出错");
               break;
           }
           if (map.get("trade_status")!=null&&map.get("trade_status").equals("TRADE_SUCCESS")){
               result=new Result(true,"支付成功");
               orderService.updateOrderStatus(out_trade_no,map.get("trade_no"));
               break;
           }
            if (map.get("trade_status")!=null&&map.get("trade_status").equals("TRADE_CLOSED")){
                result=new Result(false,"未付款交易超时关闭，或支付完成后全额退款");
                break;
            }
            if (map.get("trade_status")!=null&&map.get("trade_status").equals("TRADE_FINISHED")){
                result=new Result(false,"交易结束不可退款");
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //设置超时时间为5分钟
            x++;
            if (x>=100){
                result=new Result(false,"二维码超时");
            }

        }
        return result;
    }
}
