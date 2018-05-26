package com.chen.utils.wechat.xmlObj;

/**
 * 描述：
 *
 * @author chen_q_i@163.com
 * 2018/5/10 : 16:23.
 * @version : 1.0
 */
public class WithdrawResult {
    private String return_code ;
    private String return_msg ;
    private String result_code ;
    private String err_code ;
    private String err_code_des ;
    private String mch_id;
    private String pub_key ;
    private String partner_trade_no ;
    private String payment_no ;
    private String cmms_amt ;
    private String nonce_str ;

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    private int amount ;

    @Override
    public String toString() {
        return "WithdrawResult{" +
                "return_code='" + return_code + '\'' +
                ", return_msg='" + return_msg + '\'' +
                ", result_code='" + result_code + '\'' +
                ", err_code='" + err_code + '\'' +
                ", err_code_des='" + err_code_des + '\'' +
                ", mch_id='" + mch_id + '\'' +
                ", pub_key='" + pub_key + '\'' +
                ", partner_trade_no='" + partner_trade_no + '\'' +
                ", payment_no='" + payment_no + '\'' +
                ", cmms_amt='" + cmms_amt + '\'' +
                ", amount=" + amount +
                '}';
    }

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getErr_code() {
        return err_code;
    }

    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }

    public String getErr_code_des() {
        return err_code_des;
    }

    public void setErr_code_des(String err_code_des) {
        this.err_code_des = err_code_des;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getPub_key() {
        return pub_key;
    }

    public void setPub_key(String pub_key) {
        this.pub_key = pub_key;
    }

    public String getPartner_trade_no() {
        return partner_trade_no;
    }

    public void setPartner_trade_no(String partner_trade_no) {
        this.partner_trade_no = partner_trade_no;
    }

    public String getPayment_no() {
        return payment_no;
    }

    public void setPayment_no(String payment_no) {
        this.payment_no = payment_no;
    }

    public String getCmms_amt() {
        return cmms_amt;
    }

    public void setCmms_amt(String cmms_amt) {
        this.cmms_amt = cmms_amt;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
