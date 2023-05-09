package com.itheima.reggie.config;

import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;

import java.io.InputStream;

/*
 *
 * 微信支付配置类
 */

public  class WeiXinConfig extends WXPayConfig {
    @Override
    public String getAppID() {
        return "wxsssssssssssss17";
    }

    @Override
    public String getMchID() {
        return "1ssssssssssssssss2";
    }

    @Override
    public String getKey() {
        return "Txxxxxxxxxxxxxxxxb";
    }

    @Override
    public InputStream getCertStream() {
        return null;
    }

    /**
     * 微信的支付域名
     * @return
     */
    @Override
    public IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain() {
            public void report(String domain, long elapsedTimeMillis, Exception
                    ex) {
            }
            public DomainInfo getDomain(WXPayConfig config) {
                return new DomainInfo("api.mch.weixin.qq.com",true);
            }
        };
    }
}
