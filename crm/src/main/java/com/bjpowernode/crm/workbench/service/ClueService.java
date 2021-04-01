package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface ClueService {
    boolean saveClue(Clue clue);

    PaginationVo<Clue> pageList(Map<String, Object> map);

    Clue getDetail(String id);

    List<Activity> getActivityByClueId(String id);

    boolean unbund(String id);

    List<Activity> searchActivity(Map<String, String> map);

    boolean bund(String cid,String aids[]);

    List<Activity> SearchActivityByaname(String aname);

    boolean convert(String clueId, Tran tran, String createBy);
}
