package com.bjpowernode.crm.workbench.web.controller;


import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.TransactionService;
import com.bjpowernode.crm.workbench.service.imlp.TransactionServiceImpl;
import javafx.application.Application;

import javax.lang.model.element.NestingKind;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入交易模块");
        String path = request.getServletPath();
        if ("/workbench/transaction/GetUserList.do".equals(path)){
            GetUserList(request,response);
        }else if ("/workbench/transaction/GetMarketActivity.do".equals(path)){
            GetMarketActivity(request,response);
        }else if ("/workbench/transaction/GetContactsByName.do".equals(path)){
            GetContactsByName(request,response);
        }else if ("/workbench/transaction/getCustomerName.do".equals(path)){
            getCustomerName(request,response);
        }else if ("/workbench/transaction/save.do".equals(path)){
            save(request,response);
        }else if ("/workbench/transaction/getTranPageList.do".equals(path)){
            getTranDetail(request,response);
        } else if ("/workbench/transaction/detail.do".equals(path)){
            getDetail(request,response);
        }else if ("/workbench/transaction/getTranHistoryList.do".equals(path)){
            getTranHistoryList(request,response);
        }else if ("/workbench/transaction/changeStage.do".equals(path)){
            changeStage(request,response);
        }else if ("/workbench/transaction/getCharts.do".equals(path)){
            getCharts(request,response);
        }

    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {
        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        Map<String,Object> map = ts.getCharts();
        PrintJson.printJsonObj(response,map);

    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editBy = ((User)(request.getSession().getAttribute("user"))).getName();

        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        Tran tran = new Tran();

        tran.setId(id);
        tran.setStage(stage);
        tran.setMoney(money);
        tran.setExpectedDate(expectedDate);
        tran.setEditTime(DateTimeUtil.getSysTime());
        tran.setEditBy(editBy);
        Map<String,String> pMap = (Map<String,String>)this.getServletContext().getAttribute("pMap");
        tran.setPossibility(pMap.get(stage));

        Map<String,Object> map = ts.changeStage(tran);
        PrintJson.printJsonObj(response,map);

    }

    private void getTranHistoryList(HttpServletRequest request, HttpServletResponse response) {
        String tranId = request.getParameter("tranId");
        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        List<TranHistory> tranHistoryList = ts.getTranHistoryList(tranId);
        List<TranHistory> tranHistoryList1 = new ArrayList<>();
        ServletContext application = this.getServletContext();
        Map<String,Object> pmap =(Map<String, Object>) application.getAttribute("pMap");
        for (TranHistory tranHistory:tranHistoryList){
            String possibility = (String) pmap.get(tranHistory.getStage());
            tranHistory.setPossibility(possibility);
            tranHistoryList1.add(tranHistory);
        }
        //request.setAttribute("tranHistoryList1",tranHistoryList1);
        PrintJson.printJsonObj(response,tranHistoryList1);




    }

    private void getDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        Tran tran = ts.getDetail(id);
        //处理可能性
        ServletContext appllication = this.getServletContext();

        Map<String,Object> pmap = (Map<String, Object>) appllication.getAttribute("pMap");
        String possibility = (String) pmap.get(tran.getStage());

        request.setAttribute("tran",tran);
        request.setAttribute("possibility",possibility);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);


    }

    //交易查询
    private void getTranDetail(HttpServletRequest request, HttpServletResponse response) {
        String pageNostr =request.getParameter("pageNo");
        String pageSizestr =request.getParameter("pageSize");

        String name =request.getParameter("name");
        String stage =request.getParameter("stage");
        String type =request.getParameter("type");
        String source =request.getParameter("source");

        String owner =request.getParameter("owner");
        String contactsFullname =request.getParameter("contactsFullname");
        String customerName =request.getParameter("customerName");

        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());

        Integer pageNo = Integer.parseInt(pageNostr);
        Integer pageSize = Integer.parseInt(pageSizestr);
        Integer skipCount = (pageNo-1)*pageSize;

        Map<String,Object> map = new HashMap<>();
        map.put("pageSize",pageSize);
        map.put("skipCount",skipCount);
        map.put("name",name);
        map.put("stage",stage);
        map.put("type",type);
        map.put("source",source);
        map.put("owner",owner);
        map.put("contactsFullname",contactsFullname);
        map.put("customerName",customerName);

        PaginationVo<Tran> vo= ts.getTranPageList(map);

        PrintJson.printJsonObj(response,vo);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");

        String customerName = request.getParameter("customerName");

        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");

        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();



        Tran tran = new Tran();
        tran.setId(UUIDUtil.getUUID());
        tran.setOwner(owner);
        tran.setMoney(money);
        tran.setName(name);
        tran.setExpectedDate(expectedDate);
        tran.setStage(stage);
        tran.setType(type);
        tran.setSource(source);
        tran.setActivityId(activityId);
        tran.setContactsId(contactsId);
        tran.setCreateBy(createBy);
        tran.setCreateTime(createTime);
        tran.setDescription(description);
        tran.setContactSummary(contactSummary);
        tran.setNextContactTime(nextContactTime);

        //根据客户名称查询客户id,再放入交易对象中,没有该客户新建一个
        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        boolean flag = ts.save(tran,customerName);
        if (flag){
            //保存交易信息
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }

    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        List<String> cusname = ts.getCustomerName(name);
        PrintJson.printJsonObj(response,cusname);

    }

    private void GetContactsByName(HttpServletRequest request, HttpServletResponse response) {
        String cname = request.getParameter("cname");
        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        List<Contacts> contactsList = ts.GetContactsByName(cname);
        PrintJson.printJsonObj(response,contactsList);

    }

    private void GetMarketActivity(HttpServletRequest request, HttpServletResponse response) {
        String activityName = request.getParameter("aname");
        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        List<Activity> activityList =ts.GetMarketActivity(activityName);
        PrintJson.printJsonObj(response,activityList);
    }

    private void GetUserList(HttpServletRequest request,HttpServletResponse response) {

        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        List<User> userList = ts.GetUserList();
        PrintJson.printJsonObj(response,userList);
    }


}
