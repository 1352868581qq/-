package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.MD5Util;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到用户控制器");
        String path = request.getServletPath();
        if ("/settings/user/login.do".equals(path)){

            login(request,response);

        }else if ("/settings/user/xxx.do".equals(path)){
            login(request,response);
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("登陆操作");
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        //将密码转为MD5得密文格式
        loginPwd = MD5Util.getMD5(loginPwd);
        String ip = request.getRemoteAddr();
        System.out.println("ip---"+ip);

        //未来业务层开发，统一使用代理类形态得接口对象
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        try {
            User user = us.login(loginAct,loginPwd,ip);
            request.getSession().setAttribute("user",user);

            //如歌程序执行到这，说明业务层没有为controller抛出任何异常
            //表示登录成功
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("success",true);
            PrintJson.printJsonObj(response, map);
            //PrintJson.printJsonFlag(response,true);

        }catch (Exception e){
            e.printStackTrace();
            //程序执行了catch块，表示登录失败
            String msg = e.getMessage();
            System.out.println(msg);


            /*
                作为controller,需要为ajax
            */

            Map<String,Object> map = new HashMap<String,Object>();
            map.put("success",false);
            map.put("msg",msg);
            PrintJson.printJsonObj(response, map);

        }
    }
}
