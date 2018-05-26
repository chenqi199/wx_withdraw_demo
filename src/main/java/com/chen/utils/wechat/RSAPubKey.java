package com.chen.utils.wechat;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 描述：获取rsa 公钥
 *
 * @author chen_q_i@163.com
 * 2018/5/9 : 18:21.
 * @version : 1.0
 */
public class RSAPubKey {

    /**
     * 商户号
     */
    @XStreamAlias("mch_id")
    private String mch_id;
    /**
     * 商户企业付款单号
     */
    @XStreamAlias("partner_trade_no")
    private String partner_trade_no;
    /**
     * 随机字符串
     */
    @XStreamAlias("nonce_str")
    private String nonce_str;
    /**
     * 签名
     */
    private String sign;
    /**
     * 签名类型
     */
    private String sign_type;

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getPartner_trade_no() {
        return partner_trade_no;
    }

    public void setPartner_trade_no(String partner_trade_no) {
        this.partner_trade_no = partner_trade_no;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }
}
