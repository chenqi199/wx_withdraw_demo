
### 微信企业付款到银行卡java后台开发


[企业付款到银行卡文档](https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=24_2)


### 这个模块已经上线，只要修改配置就可以使用
![image.png](https://upload-images.jianshu.io/upload_images/3437040-13c19f6babc63855.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 代码已上传github仓库 [git 传送门](https://github.com/chenqi199/wx_withdraw_demo)
使用的是maven+jdk1.8+sprinjunit，

#### 现在的配置是假的！假的！假的，跑不起来，要修改为你自己的配置

![image.png](https://upload-images.jianshu.io/upload_images/3437040-42db7087602c65f1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#### 单元测试修改参数
![image.png](https://upload-images.jianshu.io/upload_images/3437040-230d857e236465bd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


下面挖一下坑吧
==================
#### 1 双向证书
文档里有要求请求需要双向证书
我们比较常见的证书都是单向证书，那双向证书是什么呢？
[SSL双向认证和SSL单向认证的区别](https://www.jianshu.com/p/fb5fe0165ef2)

#### 我们需要在http中加载wx颁发的证书
[官方文档 获取商户证书](https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=4_3)
- 在项目中我使用的是httpclient加载证书
```java

package com.chen.utils.wxHttpclient;

import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * 描述：
 *
 * @author chen_q_i@163.com
 * 2018/5/15 : 14:06.
 * @version : 1.0
 */
public class CertHttpUtil {
    
    public CloseableHttpClient certHttpUtil(String mchId, String certPath) throws Exception {
        System.out.println("path =========="+certPath);

        ConnectionKeepAliveStrategy connectionKeepAliveStrategy = (httpResponse, httpContext) -> {
            // tomcat默认keepAliveTimeout为20s
            return 30 * 1000;
        };
        // 证书密码，默认为商户ID
        String key = mchId;
        // 证书的路径
        String path = certPath;
        // 指定读取证书格式为PKCS12
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        // 读取本机存放的PKCS12证书文件
        FileInputStream instream = new FileInputStream(new File(path));
        try {
            // 指定PKCS12的密码(商户ID)
            keyStore.load(instream, key.toCharArray());
        } finally {
            instream.close();
        }
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, key.toCharArray()).build();
        SSLConnectionSocketFactory sslsf =
                new SSLConnectionSocketFactory(sslcontext, new String[] {"TLSv1"}, null,
                        SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
       return   HttpClients.custom().setSSLSocketFactory(sslsf).setKeepAliveStrategy(connectionKeepAliveStrategy).build();
    }
    
}

````
#### 然后在applicationContext.xml注册，提现业务统一使用这个httpclient

```
<bean id="certHttpUtil" class="com.chen.utils.wxHttpclient.CertHttpUtil"/>
    <bean id="httpClientWx" factory-bean="certHttpUtil" factory-method="certHttpUtil">
        <constructor-arg name="mchId" value="${mchId}"/>
        <constructor-arg name="certPath" value="${certPath}"/>
    </bean>

```
##### 使用方式
```

package com.chen.utils.wechat;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 描述：数据处理工具
 *
 * @author chen_q_i@163.com
 * 2018/5/10 : 10:46.
 * @version : 1.0
 */
@Component
public class WXWithdrawTools {

    Logger log = LoggerFactory.getLogger(WXWithdrawTools.class);
    public static final String GET_PUBLIC_KEY_URL = "https://fraud.mch.weixin.qq.com/risk/getpublickey";
    public static final String WITHDRAW = "https://api.mch.weixin.qq.com/mmpaysptrans/pay_bank";

    @Autowired
    private CloseableHttpClient httpClientWx;

    @Autowired
    @Qualifier("requestConfig")
    private RequestConfig requestConfig;
    /**
     * 对象转xml
     * @param obj
     * @return
     */
    public  String getXMLStringForObj(Object obj) {
        //解决XStream对出现双下划线的bug
        XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));
        xStreamForRequestPostData.alias("xml", obj.getClass());
        xStreamForRequestPostData.processAnnotations(obj.getClass());
        //将要提交给API的数据对象转换成XML格式数据Post给API
        return xStreamForRequestPostData.toXML(obj);
    }

    /**
     * xml转对象
     * @param XmlDoc
     * @param c
     * @param <T>
     * @return
     */
   public  <T> T changeXMLToOj(String XmlDoc,Class c){
        XStream xStream = new XStream();
        xStream.alias("xml",c);
        T returnInfo = null;
        try{
            returnInfo=  (T)xStream.fromXML(XmlDoc);
        }catch (ClassCastException e){
            e.printStackTrace();
            log.info(e.toString());
        }
        return  returnInfo;
    }

    /**
     * 发送请求获取rsa public key签名数据
     *
     * @return
     */
    public  String postParamesForUrl(String XmlObj,String url) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setConfig(requestConfig);

        //得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
        StringEntity postEntity = new StringEntity(XmlObj, "UTF-8");
        post.addHeader("Content-Type", "text/xml");
        post.setEntity(postEntity);

        String result = null;
        try (CloseableHttpResponse response = httpClientWx.execute(post)) {
            if (response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("失败--------------{}",e.toString());
        }
        log.info("===============================key=={}", result);
        return result;
    }


}



```
#### rsa加密

[rsa加密](https://www.jianshu.com/p/15353af33906)
> 关于rsa加密还有一点要提的是rsa加密每次加密的结果是不同的，具体可以看[为什么RSA公钥每次加密得到的结果都不一样？](https://blog.csdn.net/guyongqiangx/article/details/74930951)

- 提现模块中要对银行卡和银行卡用户名进行rsa加密，需要先获取公钥文件，落地并转码。因为接口默认输出PKCS#1格式的公钥，而java使用的是PKCS#8格式的
- 转码指令
            PKCS#1 转 PKCS#8:
openssl rsa -RSAPublicKey_in -in <filename> -pubout
- 将打印出来的字符串保存为新的文件，供以后加密使用
#### 获取公钥
````
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



<!--放入spring容器-->
 <bean id="rsaKeyUtils" class="com.chen.utils.wechat.RSAKeyUtils"/>
    <bean id="rsaPubK" factory-bean="rsaKeyUtils" factory-method="readLocalPubKey"/>
````
#### rsa公钥使用


````

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


````
### 加签
根据文档要求需要对数据加签（完整性校验）微信要求的是MD5加签
[md5加密](https://www.jianshu.com/p/b3581e1c527c)
加签代码
----------------------------
```
package com.chen.utils.wechat;

import java.security.MessageDigest;

/**
 * User: rizenguo
 * Date: 2014/10/23
 * Time: 15:43
 */
public class MD5 {
    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 转换字节数组为16进制字串
     * @param b 字节数组
     * @return 16进制字串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte aB : b) {
            resultSb.append(byteToHexString(aB));
        }
        return resultSb.toString();
    }

    /**
     * 转换byte到16进制
     * @param b 要转换的byte
     * @return 16进制格式
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * MD5编码
     * @param origin 原始字符串
     * @return 经过MD5加密之后的结果
     */
    public static String MD5Encode(String origin) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

}



````
- 这里需要注意的是加签的数据是需要排序的
加签工具
-----------------------
````
package com.chen.utils.wechat;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


/**
 * 签名
 *
 * @author zuoliangzhu
 */
public class Signature {
    private static final Logger L = LoggerFactory.getLogger(Signature.class);

    /**
     * 签名算法
     *
     * @param o 要参与签名的数据对象
     * @return 签名
     * @throws IllegalAccessException
     */
    public static String getSign(String key, Object o) throws IllegalAccessException {
        ArrayList<String> list = new ArrayList<String>();
        Class cls = o.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.get(o) != null && f.get(o) != "") {
                String name = f.getName();
                XStreamAlias anno = f.getAnnotation(XStreamAlias.class);
                if (anno != null) {
                    name = anno.value();
                }
                list.add(name + "=" + f.get(o) + "&");

            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + key;
        System.out.println("签名数据：" + result);
        result = MD5.MD5Encode(result).toUpperCase();
        return result;
    }

    public static String getSign(String key, Map<String, Object> map) {
        ArrayList<String> list = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != "") {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + key;
        result = MD5.MD5Encode(result).toUpperCase();
        return result;
    }


}


````
#### 到这里准备工作基本完成有三点要注意
- 获取的rsa公钥要转码
- 保证配置文件和项目的编码格式是utf-8的，不然加密会出现问题
- 要用使用带客户端证书的https请求微信的提现服务

#### 单元测试
注意
- 提现是以分为单位的
- 提现手续费最低为一元
- 保证企业运营账户是有余额的

```


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
        withdraw.setAmount(1);
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



````
到这里就基本完成了一次提现。由于业务代码涉及到公司的业务不能公开，请见谅！
#### 这里需要注意的是由于涉及到资金怎么小心都不为过，短信验证，身份校验，分布式锁能用就用一定要小心再小心，提现保留详细的记录，包括错误信息



              


