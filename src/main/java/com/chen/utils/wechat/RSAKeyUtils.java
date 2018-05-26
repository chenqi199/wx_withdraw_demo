package com.chen.utils.wechat;

import com.chen.context.DefParamConfig;

import com.chen.utils.UUidUtils;
import com.chen.utils.wechat.xmlObj.ReturnRsaXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.Paths.get;

/**
 * 描述：
 *
 * @author chen_q_i@163.com
 * 2018/5/9 : 18:43.
 * @version : 1.0
 */

public class RSAKeyUtils {


    Logger log = LoggerFactory.getLogger(RSAKeyUtils.class);
    private static String PUBLIC_KEY_FILE_NAME = "public.pem";

    @Autowired
    private WXWithdrawTools WXWithdrawTools;
    @Autowired
    @Qualifier("defParamConfig")
    private DefParamConfig defParamConfig;

    /**
     * 第一次从微信拿到公钥，落地生成本地文件 需要转PKCS#8
     *
     * @return
     * @throws IllegalAccessException
     * @throws IOException
     */
    private String getPubKeyForRemote() throws IllegalAccessException, IOException {
        RSAPubKey rsaPubKey = new RSAPubKey();
        rsaPubKey.setMch_id(defParamConfig.getMchId());
        rsaPubKey.setNonce_str(UUidUtils.getUUid());
        String sign = Signature.getSign(defParamConfig.getApiKey(), rsaPubKey);
        rsaPubKey.setSign(sign);
        String rasPubXml = WXWithdrawTools.getXMLStringForObj(rsaPubKey);
        log.info("get pub xml = === {}", rasPubXml);
        String result = WXWithdrawTools.postParamesForUrl(rasPubXml, WXWithdrawTools.GET_PUBLIC_KEY_URL);
        if (null != result) {
            ReturnRsaXml returnRSA = WXWithdrawTools.changeXMLToOj(result, ReturnRsaXml.class);
            log.info(" return code == {}", returnRSA.getReturn_code());
            if ("SUCCESS".equals(returnRSA.getReturn_code())) {
                savePubKeyToLocal(returnRSA.getPub_key());
            } else if ("FALL".equals(returnRSA.getReturn_code())) {
                log.info(" weixin return fall ==={}", returnRSA.toString());
            }
        }
        return "";
    }

    /**
     * 将公钥保存到本地
     *
     * @param pubKey
     */
    private void savePubKeyToLocal(String pubKey) {
        String classPath = this.getClass().getResource("/").getPath();
        log.info(classPath);
        File file = new File(classPath + PUBLIC_KEY_FILE_NAME);
        String absolutePath = file.getAbsolutePath();
        System.out.println(absolutePath);
        Path path = get(absolutePath);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(pubKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取本地公钥
     * 将公钥放入spring容器，这样就不必每次用的时候都要读取
     *
     * @return
     * @throws IOException
     */
    public String readLocalPubKey() throws IOException {

        File file = new File(defParamConfig.getRsaPublicKeyPath() + PUBLIC_KEY_FILE_NAME);
        String absolutePath = file.getAbsolutePath();
        List<String> lines = Files.readAllLines(Paths.get(absolutePath), StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (line.charAt(0) == '-') {
                continue;
            } else {
                sb.append(line);
                sb.append('\r');
            }
        }
        return sb.toString();
    }


}

