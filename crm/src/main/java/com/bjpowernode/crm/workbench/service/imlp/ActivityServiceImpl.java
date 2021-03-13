package com.bjpowernode.crm.workbench.service.imlp;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    @Override
    public boolean save(Activity activity) {
        boolean falg = true;
        int count = activityDao.save(activity);
        if (count != 1){
            falg=false;
        }
        return falg;
    }

    @Override
    public PaginationVo<Activity> pageList(Map<String,Object> map) {
        //取得total
        int total = activityDao.getTotalByCondition(map);
        //取得dataList
        List<Activity> dataList = activityDao.getActivityListByCondition(map);
        //将total和datallist封装到vo中
        PaginationVo<Activity> vo = new PaginationVo<>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        return vo;
    }

    @Override
    public boolean deleteActivity(String[] ids) {
        boolean flag = true;
        //删除之前先将关联的备注信息查询出来，备注也要删除
        int count1 = activityRemarkDao.getCountByAids(ids);
        int count2 = activityRemarkDao.deleteByAids(ids);
        System.out.println(count1);
        System.out.println(count2);

        if (count1 !=count2){
            flag = false;
        }

        int count = activityDao.deleteActivity(ids);
        System.out.println(count);
        if (count ==ids.length){
            flag = true;
        }
        return flag;
    }

    @Override
    public Map<String,Object> getUserListAndActivity(String id) {
        Map<String,Object> map = new HashMap();
        Activity activity = activityDao.getActivity(id);
        List uList = userDao.getUserlist();
        map.put("uList",uList);
        map.put("activity",activity);
        return map;

    }

    @Override
    public boolean updateActivity(Activity activity) {
        boolean flag = false;
        int count = activityDao.updateActivity(activity);
        System.out.println(count);
        if (count ==1){
            flag = true;
        }
        return flag;
    }

    @Override
    public Activity getDetail(String id) {
        Activity activity = activityDao.getDetail(id);
        return activity;
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String id) {
        System.out.println("进入IMPL");
        List<ActivityRemark> arList = activityRemarkDao.getRemarkListByAid(id);
        return arList;
    }

    @Override
    public boolean deleteRemark(String id) {
        boolean flag = false;
        int count = activityRemarkDao.deleteRemark(id);
        if (count ==1){
            flag=true;
        }
        return flag;
    }

    @Override
    public Map<String, Object> saveRemark(ActivityRemark ar) {
        Map<String,Object> map = new HashMap();
        boolean flag = false;
        int count = activityRemarkDao.saveRemark(ar);
        if (count ==1){
            flag = true;
        }
        map.put("success",flag);
        map.put("ar",ar);
        return map;
    }

    @Override
    public boolean editRemark( ActivityRemark ar) {
        boolean flag = false;
        int count = activityRemarkDao.editRemark(ar);
        if (count ==1){
            flag = true;
        }
        return flag;
    }


}
