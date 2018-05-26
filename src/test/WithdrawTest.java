import com.chen.context.DefParamConfig;
import com.chen.model.Withdraw;
import com.chen.utils.UUidUtils;
import com.chen.utils.wechat.RSAKeyUtils;
import com.chen.utils.wechat.RSAUtils;
import com.chen.utils.wechat.Signature;
import com.chen.utils.wechat.WXWithdrawTools;
import com.chen.utils.wechat.xmlObj.WithdrawInfo;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;


import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.security.PublicKey;

/**
 * 描述：
 *
 * @author chen_q_i@163.com
 * 2018/5/16 : 9:42.
 * @version : 1.0
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class WithdrawTest {

    Logger log = LoggerFactory.getLogger(WithdrawTest.class);
    @Autowired
    private RSAKeyUtils rsaKeyUtils;
    @Autowired
    private DefParamConfig defParamConfig;
    @Autowired
    private CloseableHttpClient httpClientWx;


    @Autowired
    private RSAUtils rsaUtils;
    @Autowired
    private WXWithdrawTools wxWithdrawTools;

    @Autowired
    @Qualifier("rsaPubK")
    private String pubKey;

    @Test
    public void getPubKey() throws IOException, IllegalAccessException {
        log.info("pub key =={}", "------------------------------------");
        log.info("pub key =={}", pubKey);
    }

    @Test
    public void usePubRSAKey() throws Exception {

        PublicKey publicKey = rsaUtils.loadPublicKey(pubKey);
        rsaUtils.printPublicKeyInfo(publicKey);
        String scerite = rsaUtils.encryptData("6226200107123722");
        log.info("card number =={}", scerite);
        String encrypt = rsaUtils.encrypt("6226200107123722".getBytes(), publicKey);
        log.info("encrypt===={}", encrypt);

    }

    @Test
    public void getResult() throws Exception {
        Withdraw withdraw = new Withdraw();
        withdraw.setDesc("cq提现测试");
        withdraw.setEncTrueName("真实姓名");
        withdraw.setBankCode("1006");
        withdraw.setEncBankNo("真实银行卡");
//        提现1分
        withdraw.setAmount(2);
        Double f = 8 / 100D;
        Double ceil = Math.ceil(withdraw.getAmount() * f);
        int service = ceil.intValue();

        withdraw.setTrueAmount(withdraw.getAmount() - service);

        String withdId = UUidUtils.getUUid();
        withdraw.setWithdrawId(withdId);
//                插入支付未完成状态提现订单
//        withdrawService.insertSelective(withdraw);
        WithdrawInfo withdrawInfo = new WithdrawInfo();
        withdrawInfo.setAmount(withdraw.getTrueAmount());
        withdrawInfo.setBank_code(withdraw.getBankCode());
        String encBankNo = rsaUtils.encryptData(withdraw.getEncBankNo());
        String encTrueName = rsaUtils.encryptData(withdraw.getEncTrueName());
        withdrawInfo.setDesc(withdraw.getDesc());
        withdrawInfo.setEncBankNo(encBankNo);
        withdrawInfo.setEncTrueName(encTrueName);
        withdrawInfo.setMch_id(defParamConfig.getMchId());
        withdrawInfo.setNonce_str(UUidUtils.getUUid());
        withdrawInfo.setPartner_trade_no(withdId);


        String apiKey = defParamConfig.getApiKey();
        log.info("sign key ============{}", apiKey);
        String sgin = Signature.getSign(defParamConfig.getApiKey(), withdrawInfo);
        withdrawInfo.setSign(sgin);
        log.debug("sign====={}", sgin);
        String xmlWithdrawInfo = wxWithdrawTools.getXMLStringForObj(withdrawInfo);
        log.info("提现参数为======{}", xmlWithdrawInfo);
        String response = wxWithdrawTools.postParamesForUrl(xmlWithdrawInfo, WXWithdrawTools.WITHDRAW);
        log.info("提现结果为==={}", response);
    }

}
