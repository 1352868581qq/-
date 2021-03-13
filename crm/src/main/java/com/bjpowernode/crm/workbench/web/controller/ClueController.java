package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.ContactsActivityRelation;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.imlp.ClueServiceImmpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入线索模块");
        String path = request.getServletPath();
        if ("/workbench/clue/save.do".equals(path)){
            saveClue(request,response);

        }else if ("/workbench/clue/pageList.do".equals(path)){
            pageList(request,response);

        }else if ("/workbench/clue/detail.do".equals(path)){
            detail(request,response);

        } else if ("/workbench/clue/getActivityByClueId.do".equals(path)){
            getActivityByClueId(request,response);
        } else if ("/workbench/clue/unbund.do".equals(path)){
            unbund(request,response);
        } else if ("/workbench/clue/searchActivity.do".equals(path)){
            searchActivity(request,response);
        }else if ("/workbench/clue/bund.do".equals(path)){
            bund(request,response);
        }

    }
    //关联活动
    private void bund(HttpServletRequest request, HttpServletResponse response) {

        String cid = request.getParameter("cid");
        String aids[] = request.getParameterValues("aid");

        ClueService cs = (ClueService)ServiceFactory.getService(new ClueServiceImmpl());
        boolean flag = cs.bund(cid,aids);
        PrintJson.printJsonFlag(response,flag);

    }

    //通过模糊查询未关联市场活动
    private void searchActivity(HttpServletRequest request, HttpServletResponse response) {
        String clueId = request.getParameter("clueId");
        String aname = request.getParameter("aname");
        Map<String,String> map =  new HashMap<>();
        map.put("clueId",clueId);
        map.put("aname",aname);
        ClueService cs = (ClueService)ServiceFactory.getService(new ClueServiceImmpl());
        List<Activity> alist = cs.searchActivity(map);
        PrintJson.printJsonObj(response,alist);


    }

    //解除绑定操作
    private void unbund(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImmpl());
        boolean flag = cs.unbund(id);
        PrintJson.printJsonFlag(response,flag);

    }

    //获取线索相关的活动市场信息
    private void getActivityByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("----------------------------------------------------------");
        String id = request.getParameter("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImmpl());
        List<Activity> alist = cs.getActivityByClueId(id);
        System.out.println(alist);
        PrintJson.printJsonObj(response,alist);


    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImmpl());
        Clue clue = cs.getDetail(id);

        request.setAttribute("clue",clue);
        request.getRequestDispatcher("detail.jsp").forward(request,response);



    }
    //分页查询
    private void pageList(HttpServletRequest request, HttpServletResponse response) {

        String fullname = request.getParameter("fullname");
        String company  = request.getParameter("company");
        String phone    = request.getParameter("phone");
        String source   = request.getParameter("source");
        String owner    = request.getParameter("owner");
        String mphone   = request.getParameter("mphone");
        String state    = request.getParameter("state");

        String pageNostr   = request.getParameter("pageNo");
        String pageSizestr = request.getParameter("pageSize");
        int pageNo = Integer.parseInt(pageNostr);
        int pageSize = Integer.parseInt(pageSizestr);
        int skipCount = (pageNo-1)*pageSize;
        Map<String,Object> map = new HashMap<>();
        map.put("fullname",fullname);
        map.put("company",company);
        map.put("phone",phone);
        map.put("source",source);
        map.put("owner",owner);
        map.put("mphone",mphone);
        map.put("state",state);
        map.put("pageSize",pageSize);
        map.put("skipCount",skipCount);

        ClueService cs = (ClueService)ServiceFactory.getService(new ClueServiceImmpl());
        PaginationVo<Clue> vo = cs.pageList(map);

        PrintJson.printJsonObj(response,vo);

    }

    //创建新的线索
    private void saveClue(HttpServletRequest request, HttpServletResponse response) {
        //从前端获取数据
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");
        //三个特殊值，不需要从前端获取
        String id = UUIDUtil.getUUID();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        Clue clue = new Clue();
        //将数据存入clue
        clue.setId(id);
        clue.setFullname(fullname);
        clue.setAppellation(appellation);
        clue.setOwner(owner);
        clue.setCompany(company);
        clue.setJob(job);
        clue.setEmail(email);
        clue.setPhone(phone);
        clue.setWebsite(website);
        clue.setMphone(mphone);
        clue.setState(state);
        clue.setSource(source);
        clue.setCreateBy(createBy);
        clue.setCreateTime(createTime);
        clue.setDescription(description);
        clue.setContactSummary(contactSummary);
        clue.setNextContactTime(nextContactTime);
        clue.setAddress(address);


        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImmpl());
        boolean flag = cs.saveClue(clue);
        PrintJson.printJsonFlag(response,flag);
    }
}
