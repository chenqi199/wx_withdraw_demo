package com.chen.context;

public class DefParamConfig {

    private String defNotifyUploadUrl;

    private String defSchoolPic;

    private String appid;

    private String secret;

    /**
     * 小程序的 商户ID
     */
    private String mchId;

    private byte serviceCharge;
    /**
     * 商户API秘钥
     */
    private String apiKey;

    private  String rsaPublicKeyPath;

    public String getRsaPublicKeyPath() {
        return rsaPublicKeyPath;
    }

    public void setRsaPublicKeyPath(String rsaPublicKeyPath) {
        this.rsaPublicKeyPath = rsaPublicKeyPath;
    }

    public String getDefBanel2() {
        return defBanel2;
    }

    public void setDefBanel2(String defBanel2) {
        this.defBanel2 = defBanel2;
    }

    private String defBanel2;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }



    public byte getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(byte serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getDefSchoolPic() {
        return defSchoolPic;
    }

    public void setDefSchoolPic(String defSchoolPic) {
        this.defSchoolPic = defSchoolPic;
    }

    public String getDefNotifyUploadUrl() {
        return defNotifyUploadUrl;
    }

    public void setDefNotifyUploadUrl(String defNotifyUploadUrl) {
        this.defNotifyUploadUrl = defNotifyUploadUrl;
    }
}