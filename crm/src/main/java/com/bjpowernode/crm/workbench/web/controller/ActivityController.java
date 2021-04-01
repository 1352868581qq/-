package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.*;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.imlp.ActivityServiceImpl;
import netscape.javascript.JSObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入市场模块");
        //获取访问页面的相对路径（别名）
        String path = request.getServletPath();

        if ("/workbench/activity/getUserlist.do".equals(path)){
            getUserlist(request,response);
        }
        else if ("/workbench/activity/save.do".equals(path)){
            save(request,response);
        }else if ("/workbench/activity/pageList.do".equals(path)){
            pageList(request,response);
        }else if ("/workbench/activity/deleteActivity.do".equals(path)){
            deleteActivity(request,response);
        }else if ("/workbench/activity/getUserListAndActivity.do".equals(path)){
            getUserListAndActivity(request,response);
        }else if ("/workbench/activity/updateActivity.do".equals(path)){
            updateActivity(request,response);
        }else if ("/workbench/activity/detail.do".equals(path)){
            getDetail(request,response);
        } else if ("/workbench/activity/getRemarkListByAid.do".equals(path)){
            getRemarkListByAid(request,response);
        } else if ("/workbench/activity/deleteRemark.do".equals(path)){
            deleteRemark(request,response);
        } else if ("/workbench/activity/saveRemark.do".equals(path)){
            saveRemark(request,response);
        } else if ("/workbench/activity/updateRemark.do".equals(path)){
            editRemark(request,response);
        }


    }
    //编辑备注信息
    private void editRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(22222222);
        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)(request.getSession().getAttribute("user"))).getName();

        ActivityService as = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());

        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setEditFlag("1");
        ar.setEditTime(editTime);
        ar.setEditBy(editBy);
        boolean flag = as.editRemark(ar);
        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("ar",ar);
        PrintJson.printJsonObj(response,map);
    }

    //添加备注信息
    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        String noteContent = request.getParameter("noteContent");
        String activityId = request.getParameter("activityId");
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)(request.getSession().getAttribute("user"))).getName();

        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setActivityId(activityId);
        ar.setCreateBy(createBy);
        ar.setNoteContent(noteContent);
        ar.setCreateTime(createTime);
        ar.setEditFlag("0");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Map<String,Object> map = as.saveRemark(ar);
        PrintJson.printJsonObj(response,map);
    }

    //删除活动备注信息
    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.deleteRemark(id);
        PrintJson.printJsonFlag(response,flag);

    }

    //获取备注信息
    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("获取备注信息");
        String id = request.getParameter("activityId");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<ActivityRemark> arList = as.getRemarkListByAid(id);
        PrintJson.printJsonObj(response,arList);

    }

    //查询活动详细信息
    private void getDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        //读取活动信息和备注信息
        Activity activity =as.getDetail(id);

        request.setAttribute("activity",activity);
        request.getRequestDispatcher("detail.jsp").forward(request,response);


    }

    //修改活动信息
    private void updateActivity(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("修改活动");
        Activity activity = new Activity();
        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();

        activity.setId(id);
        activity.setOwner(owner);
        activity.setName(name);
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setCost(cost);
        activity.setDescription(description);
        activity.setEditTime(editTime);
        activity.setEditBy(editBy);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.updateActivity(activity);
        PrintJson.printJsonFlag(response,flag);
    }

    //获取修改活动信息,
    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("修改活动");
        String id = request.getParameter("id");
        //UserService us = (UserService)ServiceFactory.getService(new UserServiceImpl());
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Map<String,Object> map = as.getUserListAndActivity(id);
        PrintJson.printJsonObj(response,map);

    }

    //删除活动
    private void deleteActivity(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入删除操作");
        String id[] = request.getParameterValues("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.deleteActivity(id);

        PrintJson.printJsonFlag(response,flag);

    }

    //分页查询
    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到查询市场活动信息列表操作（结合条件查询+分页查询）");

        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        //
        String pageNostr = request.getParameter("pageNo");
        int pageNo = Integer.parseInt(pageNostr);
        //每页展示的记录数
        String pageSizestr = request.getParameter("pageSize");
        int pageSize = Integer.parseInt(pageSizestr);
        int skipCount = (pageNo-1)*pageSize;
        Map<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        /*
            前端要，市场活动信息列表
                    查询的总条数
                    业务层拿到以上两项信息之后，如果做返回
                    map
                    map.put("dataList":datalist)
                    map.put("total":total)
                    PrintHSON map -->json

                    vo
                    paginationVO<T>
                     将来分页查询，每个模块都有，所以选择一个通用的vo，
                     操作起来方便
         */
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        PaginationVo<Activity> vo = as.pageList(map);
        PrintJson.printJsonObj(response,vo);

    }
    //市场活动添加操作
    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行市场活动添加操作");
        String id= UUIDUtil.getUUID();
        String owner=request.getParameter("owner");
        String name=request.getParameter("name");
        String startDate=request.getParameter("startDate");
        String endDate=request.getParameter("endDate");
        String cost=request.getParameter("cost");
        String description=request.getParameter("description");
        //创建时间，当前系统时间
        String createTime= DateTimeUtil.getSysTime();
        //创建人：当前登录用户
        String createBy=((User)request.getSession().getAttribute("user")).getName();

        //将数据添加到实体类
        Activity activity = new Activity();
        activity.setId(id);
        activity.setOwner(owner);
        activity.setName(name);
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setCost(cost);
        activity.setDescription(description);
        activity.setCreateTime(createTime);
        activity.setCreateBy(createBy);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean falg = as.save(activity);
        PrintJson.printJsonFlag(response,falg);


    }
    //查询用户信息操作
    protected void getUserlist(HttpServletRequest request,HttpServletResponse response){

        //动态获取接口对象
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = us.getUserlist();
        PrintJson.printJsonObj(response,uList);


    }
}
