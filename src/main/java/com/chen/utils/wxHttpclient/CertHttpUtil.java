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
