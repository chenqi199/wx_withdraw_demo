package com.chen.utils.wechat;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * 描述：
 *
 * @author chen_q_i@163.com
 * 2018/5/10 : 14:23.
 * @version : 1.0
 */
@Component
public class RSAUtils {
    Logger log = LoggerFactory.getLogger(RSAUtils.class);
    private static String RSA = "RSA";

    @Autowired
    @Qualifier("rsaKeyUtils")
    private RSAKeyUtils rsaKeyUtils;
    @Autowired
    @Qualifier("rsaPubK")
    private String pubKey;

    private static final int KEYLENGTH = 2048;
    private static final int RESERVESIZE = 11;
    /**
     * 指定填充模式
     */
    private static final String CIPHERALGORITHM = "RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING";


    /**
     * 用公钥加密 <br>
     * 每次加密的字节数，不能超过密钥的长度值减去11
     *
     * @param plainBytes 需加密数据的byte数据
     * @param publicKey  公钥
     * @return 加密后的byte型数据
     */
    public String encrypt(byte[] plainBytes, PublicKey publicKey) throws Exception {
        int keyByteSize = KEYLENGTH / 8;
        int encryptBlockSize = keyByteSize - RESERVESIZE;
        int nBlock = plainBytes.length / encryptBlockSize;
        if ((plainBytes.length % encryptBlockSize) != 0) {
            nBlock += 1;
        }
        ByteArrayOutputStream outbuf = null;
        try {
            Cipher cipher = Cipher.getInstance(CIPHERALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            outbuf = new ByteArrayOutputStream(nBlock * keyByteSize);
            for (int offset = 0; offset < plainBytes.length; offset += encryptBlockSize) {
                int inputLen = plainBytes.length - offset;
                if (inputLen > encryptBlockSize) {
                    inputLen = encryptBlockSize;
                }
                byte[] encryptedBlock = cipher.doFinal(plainBytes, offset, inputLen);
                outbuf.write(encryptedBlock);
            }
            outbuf.flush();
            byte[] encryptedData = outbuf.toByteArray();
            return Base64.encodeBase64String(encryptedData);
        } catch (Exception e) {
            throw new Exception("ENCRYPT ERROR:", e);
        } finally {
            try {
                if (outbuf != null) {
                    outbuf.close();
                }
            } catch (Exception e) {
                throw new Exception("CLOSE ByteArrayOutputStream ERROR:", e);
            }
        }
    }




    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public PublicKey loadPublicKey(String publicKeyStr) throws Exception {
        log.info("string key========={}", publicKeyStr);
        try {
            byte[] buffer = decodeBase64(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    /***
     * decode by Base64
     */
    public byte[] decodeBase64(String input) throws Exception {
        return Base64.decodeBase64(input);

    }

    /**
     * encodeBase64
     */
    public String encodeBase64(byte[] input) throws Exception {
        return Base64.encodeBase64String(input);
    }

    /**
     * 打印公钥信息
     *
     * @param publicKey
     */
    public  void printPublicKeyInfo(PublicKey publicKey) {
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
        log.info("----------RSAPublicKey----------");
        log.info("Modulus.length=" + rsaPublicKey.getModulus().bitLength());
        log.info("Modulus=" + rsaPublicKey.getModulus().toString());
        log.info("PublicExponent.length=" + rsaPublicKey.getPublicExponent().bitLength());
        log.info("PublicExponent=" + rsaPublicKey.getPublicExponent().toString());
    }

    public String encryptData(String data) throws Exception {
        log.debug("pubKey================={}", pubKey);
        PublicKey publicKey = loadPublicKey(pubKey);
        return encrypt(data.getBytes("UTF-8"), publicKey);
    }

   

}
