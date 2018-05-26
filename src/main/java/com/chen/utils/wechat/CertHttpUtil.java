//package com.chen.utils.wechat;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.conn.ConnectionKeepAliveStrategy;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.ssl.SSLContexts;
//
//import javax.net.ssl.SSLContext;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.channels.FileChannel;
//import java.security.KeyStore;
//
///**
// * 描述：
// *
// * @author chen_q_i@163.com
// * 2018/5/15 : 14:06.
// * @version : 1.0
// */
//public class CertHttpUtil {
//
//
//
//
//    public CloseableHttpClient certHttpUtil(String mchId, String certPath) throws Exception {
//        System.out.println("path =========="+certPath);
//
//        ConnectionKeepAliveStrategy connectionKeepAliveStrategy = new ConnectionKeepAliveStrategy() {
//            @Override
//            public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
//                return 30 * 1000; // tomcat默认keepAliveTimeout为20s
//            }
//        };
//        // 证书密码，默认为商户ID
//        String key = mchId;
//        // 证书的路径
//        String path = certPath;
//        // 指定读取证书格式为PKCS12
//        KeyStore keyStore = KeyStore.getInstance("PKCS12");
//        // 读取本机存放的PKCS12证书文件
//        FileInputStream instream = new FileInputStream(new File(path));
//        try {
//            // 指定PKCS12的密码(商户ID)
//            keyStore.load(instream, key.toCharArray());
//        } finally {
//            instream.close();
//        }
//        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, key.toCharArray()).build();
//        SSLConnectionSocketFactory sslsf =
//                new SSLConnectionSocketFactory(sslcontext, new String[] {"TLSv1"}, null,
//                        SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
//       return   HttpClients.custom().setSSLSocketFactory(sslsf).setKeepAliveStrategy(connectionKeepAliveStrategy).build();
//    }
//
//    public static void main(String[] args) throws IOException {
//        FileInputStream fileInputStream = new FileInputStream(new File("D:\\ddweixiao\\cert\\apiclient_cert.p12"));
//
//        FileChannel channel = fileInputStream.getChannel();
//        ByteBuffer allocate = ByteBuffer.allocate((int) channel.size());
//        channel.read(allocate);
//        allocate.flip();
//        System.out.println(new String(allocate.array(),"utf-8"));
//    }
//}
