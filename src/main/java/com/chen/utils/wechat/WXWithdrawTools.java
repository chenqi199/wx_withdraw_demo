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
