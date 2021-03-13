package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ActivityService {
    boolean save(Activity activity);

    PaginationVo<Activity> pageList(Map<String,Object> map);

    boolean deleteActivity(String id[]);

    Map<String,Object> getUserListAndActivity(String id);

    boolean updateActivity(Activity activity);

    Activity getDetail(String id);

    List<ActivityRemark> getRemarkListByAid(String id);

    boolean deleteRemark(String id);

    Map<String, Object> saveRemark(ActivityRemark ar);

    boolean editRemark(ActivityRemark ar);
}
