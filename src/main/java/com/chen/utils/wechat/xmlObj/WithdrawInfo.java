package com.chen.utils.wechat.xmlObj;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 描述：
 *
 * @author chen_q_i@163.com
 * 2018/5/9 : 17:22.
 * @version : 1.0
 */
@XStreamAlias("xml")
public class WithdrawInfo {

    /**
     * 付款金额
     */
    private Integer amount;

    /**
     * 收款方开户行  https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=24_4
     */
    private String bank_code;
    /**
     * 付款说明
     */
    private String desc;

    /**
     * 收款方银行卡号 rsa加密 64位
     */
    @XStreamAlias("enc_bank_no")
    private String encBankNo;

    /**
     * 收款方用户名 rsa加密 64位
     */
    @XStreamAlias("enc_true_name")
    private String encTrueName;
    /**
     * 商户号
     */
    @XStreamAlias("mch_id")
    private String mch_id;

    /**
     * 随机字符串
     */
    @XStreamAlias("nonce_str")
    private String nonce_str;
    /**
     * 商户企业付款单号
     */
    @XStreamAlias("partner_trade_no")
    private String partner_trade_no;
    /**
     * 签名
     */
    private String sign;
    private String sign_type;

    public String getBank_code() {
        return bank_code;
    }
    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

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

    public String getEncBankNo() {
        return encBankNo;
    }

    public void setEncBankNo(String encBankNo) {
        this.encBankNo = encBankNo;
    }

    public String getEncTrueName() {
        return encTrueName;
    }

    public void setEncTrueName(String encTrueName) {
        this.encTrueName = encTrueName;
    }


    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }


//    @Override
//    public String toString() {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("appid=").append(appid)
//                .append("&body=").append(body)
//                .append("&mch_id=").append(mch_id)
//                .append("&nonce_str=").append(nonce_str)
//                .append("&notify_url=").append(notify_url)
//                .append("&out_trade_no=").append(out_trade_no)
//                .append("&sign_type=").append(sign_type)
//                .append("&spbill_create_ip=").append(spbill_create_ip)
////                    .append("&time_expire=").append(time_expire)
////                    .append("&time_start=").append(time_start)
//                .append("&total_fee=").append(total_fee)
//                .append("&trade_type=").append(trade_type)
//                .append("&key=").append("a6fd7034dddc4188b7ca69da8ef0a8b9");
//        return stringBuilder.toString();
//
//
//    }
}
