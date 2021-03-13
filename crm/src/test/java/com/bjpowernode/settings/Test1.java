package com.bjpowernode.settings;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.MD5Util;
import org.junit.Test;
import org.junit.runner.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class Test1 {
    public static void main(String[] args) {
       /* //验证失效时间
        //失效时间
        String enpireTime ="2021-02-12";
        //当前系统时间
        String currentTime = DateTimeUtil.getSysTime();
        int count = enpireTime.compareTo(currentTime);
        System.out.println(count);*/

       /* //验证账号是否锁定
        String lockState = "0";
        if ("0".equals(lockState)){
            System.out.println("账号已锁定");
        }*/

       /* //浏览器端得ip地址
        String ip = "192.168.1.3";
        //允许访问得ip地址群
        String allowIps = "192.168.1.3,192.168.1.2";
        if (allowIps.contains(ip)){
            System.out.println("有效得ip地址");
        }else {
            System.out.println("ip地址受限，请联系管理员");
        }*/

        /*
        //加密密码
        String pwd ="13005039536a";
        pwd = MD5Util.getMD5(pwd);
        System.out.println(pwd);*/
    }

    @Test
    public static void m1(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getContextPath();
        System.out.println(path);
    }
}
