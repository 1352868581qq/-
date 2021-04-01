package com.bjpowernode.crm.workbench.service.imlp;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.dao.*;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ClueService;

import javax.xml.stream.FactoryConfigurationError;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClueServiceImmpl implements ClueService {
    private ClueDao clueDao= SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao= SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private CustomerDao customerDao= SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private ContactsRemarkDao contactsRemarkDao= SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private CustomerRemarkDao customerRemarkDao= SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);
    private ContactsDao contactsDao= SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ClueRemarkDao clueRemarkDao= SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao= SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);
    private TranDao tranDao= SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao= SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    @Override
    public boolean saveClue(Clue clue) {
        boolean flag = false;
        int count = clueDao.saveClue(clue);
        if (count == 1){
            flag = true;
        }
        return flag;
    }

    @Override
    public PaginationVo<Clue> pageList(Map<String, Object> map) {
        List<Clue> clueList = clueDao.pageList(map);
        int total = clueDao.getTotal(map);
        PaginationVo<Clue> pt = new PaginationVo();
        pt.setDataList(clueList);
        pt.setTotal(total);
        return pt;
    }

    @Override
    public Clue getDetail(String id) {
        Clue clue = clueDao.getDetail(id);
        return clue;
    }

    @Override
    public List<Activity> getActivityByClueId(String id) {
        List<Activity> alist = clueDao.getActivityByClueId(id);
        return alist;
    }

    @Override
    public boolean unbund(String id) {
        boolean flag = false;
        int count = clueDao.unbund(id);
        if (count ==1){
            flag = true;
        }

        return flag;
    }

    @Override
    public List<Activity> searchActivity(Map<String, String> map) {
        List<Activity> alist = clueDao.searchActivity(map);

        return alist;
    }

    @Override
    public boolean bund(String cid,String []aids) {
        boolean flag = true;

        for(String aid:aids){

            //取得每一个aid和cid做关联
            ClueActivityRelation car = new ClueActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setActivityId(aid);
            car.setClueId(cid);

            //添加关联关系表中的记录
            int count = clueActivityRelationDao.bund(car);
            if(count!=1){
                flag = false;
            }

        }

        return flag;

    }

    @Override
    public List<Activity> SearchActivityByaname(String aname) {
        return clueActivityRelationDao.SearchActivityByaname(aname);
    }

    @Override
    public boolean convert(String clueId, Tran tran, String createBy) {
        String createTime = DateTimeUtil.getSysTime();

        boolean flag = true;
        //(1) 获取到线索id，通过线索id获取线索对象（线索对象当中封装了线索的信息）
        Clue clue = clueDao.getClueByClueId(clueId);
		//(2) 通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在！）
        String company = clue.getCompany();
        Customer customer = customerDao.getCustomerByName(company);
        if(customer ==null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setOwner(clue.getOwner());
            customer.setName(company);
            customer.setWebsite(clue.getWebsite());
            customer.setPhone(clue.getPhone());
            customer.setCreateBy(createBy);
            customer.setCreateTime(createTime);
            customer.setContactSummary(clue.getContactSummary());
            customer.setNextContactTime(clue.getNextContactTime());
            customer.setDescription(clue.getDescription());
            customer.setAddress(clue.getAddress());
            //添加客户
            int count = customerDao.save(customer);
            if (count != 1){
                flag = false;
            }

        }
		//(3) 通过线索对象提取联系人信息，保存联系人
        Contacts contacts = new Contacts();
        contacts.setId(UUIDUtil.getUUID());
        contacts.setSource(clue.getSource());
        contacts.setOwner(clue.getOwner());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setFullname(clue.getFullname());
        contacts.setEmail(clue.getEmail());
        contacts.setDescription(clue.getDescription());
        contacts.setCustomerId(customer.getId());
        contacts.setCreateTime(createTime);
        contacts.setCreateBy(createBy);
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setAppellation(clue.getAppellation());
        contacts.setAddress(clue.getAddress());
        int count2 = contactsDao.save(contacts);
        if (count2 !=1){
            flag = false;
        }
		//(4) 线索备注转换到客户备注以及联系人备注
        //查询出与该线索关联的备注信息列表
        List<ClueRemark> clueRemarkList = clueRemarkDao.getListByClueId(clueId);

        for (ClueRemark clueRemark:clueRemarkList){
            String noteContent = clueRemark.getNoteContent();
            //客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setCustomerId(customer.getId());
            customerRemark.setEditFlag("0");
            customerRemark.setNoteContent(noteContent);
            int count3 = customerRemarkDao.save(customerRemark);
            if(count3!=1){
                flag = false;
            }
            //联系人备注
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setContactsId(contacts.getId());
            contactsRemark.setEditFlag("0");
            contactsRemark.setNoteContent(noteContent);
            int count4 = contactsRemarkDao.save(contactsRemark);
            if(count4!=1){
                flag = false;
            }
        }


		//(5) “线索和市场活动”的关系转换到“联系人和市场活动”的关系

        //查询出该线索关联的市场活动，查询与市场活动关联的关系列表
        List<ClueActivityRelation> clueActivityRelationList =clueActivityRelationDao.getListByClueId(clueId);
        //遍历每一条与市场活动关联的关系记录
        for(ClueActivityRelation clueActivityRelation:clueActivityRelationList){

            String activityId = clueActivityRelation.getActivityId();
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(contacts.getId());
            int count5 =contactsActivityRelationDao.save(contactsActivityRelation);
            if (count5 !=1){
                flag = false;
            }

        }
		//(6) 如果有创建交易需求，创建一条交易

        if (tran !=null){
            int count6 = tranDao.save(tran);
            if (count6 !=1){
                flag =false;
            }
            //(7) 如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setCreateTime(createTime);
            tranHistory.setCreateBy(createBy);
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setStage(tran.getStage());
            tranHistory.setTranId(tran.getId());

            int count7 = tranHistoryDao.save(tranHistory);
            if (count7 !=1){
                flag = false;
            }

        }
		//(8) 删除线索备注
        int count8 = clueRemarkDao.delete(clueId);

		//(9) 删除线索和市场活动的关系
        int count9 = clueActivityRelationDao.delete(clueId);

		//(10) 删除线索
        int count10 = clueDao.delete(clueId);
        if (count10 !=1){
            flag = false;
        }
        return flag;
    }

}
