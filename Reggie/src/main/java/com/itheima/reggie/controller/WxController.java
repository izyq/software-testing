package com.itheima.reggie.controller;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.itheima.reggie.config.WeiXinConfig;
import com.itheima.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付
 */
@Slf4j
@RestController
@RequestMapping("/wx")
public class WxController {

    @Autowired
    private OrdersService ordersService;

    @PostMapping("/notify")
    public void notifyWx(HttpServletRequest req, HttpServletResponse resp) throws Exception {
    
    
        System.out.println("我执行了");
        String s = convertToString(req.getInputStream());
        Map<String, String> stringStringMap = WXPayUtil.xmlToMap(s);
    
        System.out.println(stringStringMap);
        //返回状态
        String result_code = stringStringMap.get("result_code");
        //订单号
        String out_trade_no = stringStringMap.get("out_trade_no");
        //流水号
        String transaction_id = stringStringMap.get("transaction_id");
        log.debug("微信支付响应");
    
        if (result_code.equals("SUCCESS")) {

            WXPay wxPay = new WXPay(new WeiXinConfig());
            Map<String, String> requsetMap = new HashMap<>();

            requsetMap.put("out_trade_no", out_trade_no);

            Map<String, String> responseMap = wxPay.orderQuery(requsetMap);
            System.out.println(responseMap);

            String trade_state = responseMap.get("trade_state");
            if (trade_state.equals("SUCCESS")) {

                ordersService.updateStatus(out_trade_no, transaction_id);

                //给微信支付一个成功的响应
                resp.setContentType("text/xml");
                String data = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
                resp.getWriter().write(data);

            }
        }
    }


    /**
     * 输入流转换为xml字符串
     * @param inputStream
     * @return
     */
    public static String convertToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inputStream.close();
        String result = new String(outSteam.toByteArray(), "utf-8");
        return result;
    }
}
