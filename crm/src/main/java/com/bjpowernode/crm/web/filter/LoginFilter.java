package com.bjpowernode.crm.web.filter;

import com.bjpowernode.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        System.out.println("进入到验证有没有登录过得过滤器");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user !=null){//登陆过
            chain.doFilter(req,resp);
        }else {
            //没登陆过
           /*
            转发和重定向写法
            转发：使用特殊的绝对路径的使用方式，这种绝对路径前面不加/项目名
            也称为内部路径   如：/login.jsp
            重定向：使用的是传统的绝对路径的写法，前面必须以/项目名开头，后面跟具体的资源路径
            /crm/login.jsp
            为什么使用重定向，使用转发不行吗？
            转发之后，路径会停留在老路径上，而不是跳转之后最新资源的路径
            我们应该为该用户跳转到登录页的同时，而将浏览器的地址栏应该设置
            为当前的登录页的路径

            */
           response.sendRedirect(request.getContextPath()+"/login.jsp");

        }
    }

    @Override
    public void destroy() {

    }
}
