package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.domain.DicType;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitListener implements ServletContextListener {
    /*
      该方法是用来监听上下文域对象的方法，当服务器启动，上下文域对象创建
      对象创建完毕后，马上执行该方法

      event：该参数能够取得监听的对象
              监听的是什么对象，就可以通过该参数能取得什么对象
              例如我们现在监听的是上下文域对象，通过该参数就可以取得上下文域对象
   */
    //
    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("服务器缓存处理数据字典开始");
        ServletContext application = event.getServletContext();

        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());
        Map<String,List<DicValue>> map = ds.getAll();
        Set<String> set = map.keySet();
        for (String key:set){

            application.setAttribute(key,map.get(key));
        }
        System.out.println("服务器缓存处理数据字典结束");

        //-------------------------------------------

        ResourceBundle bundle = ResourceBundle.getBundle("Stage2Possibility");
        Enumeration<String> keys2 = bundle.getKeys();
        Map<String,String> pMap = new HashMap<>();
        while(keys2.hasMoreElements()){
            String key = keys2.nextElement();
            String value = bundle.getString(key);
            pMap.put(key, value);
        }
        application.setAttribute("pMap", pMap);
    }



    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }
}
