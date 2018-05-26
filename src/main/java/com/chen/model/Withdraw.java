package com.chen.model;

import java.util.Date;

public class Withdraw {
    private String withdrawId;

    private Long agentId;

    private String encBankNo;

    private String encTrueName;

    private String bankCode;

    private Integer amount;

    private String desc;

    private Date ctime;
    /**
     * 扣点后可到账金额
     */
    private int trueAmount;
    /**
     * 微信后台扣除的手续费
     */
    private float  cmmsAmt;
    /**
     * 支付状态 0 未完成 1 已完成
     */
    private byte status;


    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getWithdrawId() {
        return withdrawId;
    }

    public void setWithdrawId(String withdrawId) {
        this.withdrawId = withdrawId == null ? null : withdrawId.trim();
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getEncBankNo() {
        return encBankNo;
    }

    public void setEncBankNo(String encBankNo) {
        this.encBankNo = encBankNo == null ? null : encBankNo.trim();
    }

    public String getEncTrueName() {
        return encTrueName;
    }

    public void setEncTrueName(String encTrueName) {
        this.encTrueName = encTrueName == null ? null : encTrueName.trim();
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
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
        this.desc = desc == null ? null : desc.trim();
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }
    public int getTrueAmount() {
        return trueAmount;
    }

    public void setTrueAmount(int trueAmount) {
        this.trueAmount = trueAmount;
    }

    public float getCmmsAmt() {
        return cmmsAmt;
    }

    public void setCmmsAmt(float cmmsAmt) {
        this.cmmsAmt = cmmsAmt;
    }
}