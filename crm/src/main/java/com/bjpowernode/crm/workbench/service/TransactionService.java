package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Contacts;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;

import java.util.List;
import java.util.Map;

public interface TransactionService {
    List<User> GetUserList();

    List<Activity> GetMarketActivity(String activityName);

    List<Contacts> GetContactsByName(String cname);

    List<String> getCustomerName(String name);

    boolean save(Tran tran, String customerName);

    PaginationVo<Tran> getTranPageList(Map<String, Object> map);

    Tran getDetail(String id);

    List<TranHistory> getTranHistoryList(String tranId);

    Map<String,Object> changeStage(Tran tran);

    Map<String, Object> getCharts();
}
