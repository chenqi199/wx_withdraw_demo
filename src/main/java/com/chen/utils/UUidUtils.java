package com.chen.utils;

import java.util.UUID;

/**
 * 描述：uuid 生成id
 *
 * @author chen_q_i@163.com
 * 2018/5/9 : 10:30.
 * @version : 1.0
 */
public class UUidUtils {
    public static String getUUid(){
        return UUID.randomUUID().toString().replace("-", "");
    }


     public static void main(String[] args) {
        String id  = getUUid();
        System.out.println(id);
    }
}
