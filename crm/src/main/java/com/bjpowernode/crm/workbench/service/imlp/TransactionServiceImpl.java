package com.bjpowernode.crm.workbench.service.imlp;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.dao.*;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.TransactionService;
import com.sun.javafx.collections.MappingChange;

import javax.lang.model.element.NestingKind;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionServiceImpl implements TransactionService {
    private UserDao userDao= SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
    private ActivityDao activityDao= SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ClueActivityRelationDao clueActivityRelationDao= SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ContactsDao contactsDao= SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private CustomerDao customerDao= SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private TranDao tranDao= SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao= SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    @Override
    public List<User> GetUserList() {
        return userDao.getUserlist();
    }

    @Override
    public List<Activity> GetMarketActivity(String activityName) {

        return clueActivityRelationDao.SearchActivityByaname(activityName);
    }

    @Override
    public List<Contacts> GetContactsByName(String cname) {
        return contactsDao.GetContactsByName(cname);
    }

    @Override
    public List<String> getCustomerName(String name) {
        return customerDao.getCustomerName(name);
    }

    @Override
    public boolean save(Tran tran, String customerName) {
        boolean flag = true;
        //先判断是否存在该客户
        String customerId = customerDao.getCustomerIdName(customerName);
        if (customerId == null){
            //新建一个客户
            Customer customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setOwner(tran.getOwner());
            customer.setName(customerName);
            customer.setCreateBy(tran.getCreateBy());
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setContactSummary(tran.getContactSummary());
            customer.setNextContactTime(tran.getNextContactTime());
            int count = customerDao.save(customer);
            if (count !=1){
                flag=false;
            }
            customerId=customer.getId();

        }
        tran.setCustomerId(customerId);
        int count1 = tranDao.save(tran);
        if (count1 !=1){
            flag=false;
        }

        return flag;
    }

    @Override
    public PaginationVo<Tran> getTranPageList(Map<String, Object> map) {

        int total = tranDao.getTotal(map);
        List<Tran> tranList = tranDao.getTranPageList(map);
        PaginationVo<Tran> vo = new PaginationVo<>();
        vo.setTotal(total);
        vo.setDataList(tranList);
        return vo;
    }

    @Override
    public Tran getDetail(String id) {

        return tranDao.getDetail(id);
    }

    @Override
    public List<TranHistory> getTranHistoryList(String tranId) {
        return tranHistoryDao.getTranHistoryList(tranId);
    }

    @Override
    public Map<String,Object> changeStage(Tran tran) {
        boolean flag = true;
        Map<String,Object> objectMap = new HashMap<>();
        //新增一天改变交易历史记录
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setTranId(tran.getId());
        tranHistory.setStage(tran.getStage());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setCreateBy(tran.getEditBy());
        tranHistory.setCreateTime(tran.getEditTime());
        int count = tranHistoryDao.save(tranHistory);
        if (count !=1){
            flag=false;
        }
        int count2 = tranDao.changeStage(tran);
        if (count2 !=1){
            flag =false;
        }
        objectMap.put("success",flag);
        objectMap.put("tran",tran);

        return objectMap;
    }

    @Override
    public Map<String, Object> getCharts() {
        int total = tranDao.getTranTotal();
        List<Map<String,Object>> maplist = tranDao.getChart();
        Map<String,Object> map = new HashMap<>();
        map.put("total",total);
        map.put("dataList",maplist);
        return map;
    }

}
