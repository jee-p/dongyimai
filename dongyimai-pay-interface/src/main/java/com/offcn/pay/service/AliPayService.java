package com.offcn.pay.service;

import java.util.Map;

public interface AliPayService {
    //创建二维码
    public Map createNative(String out_trate_no,String total_fee);

    public Map queryPayStatus(String out_trade_no);
}
