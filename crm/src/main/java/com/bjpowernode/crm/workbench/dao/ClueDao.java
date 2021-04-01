package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueDao {


    int saveClue(Clue clue);

    List<Clue> pageList(Map<String, Object> map);

    int getTotal(Map<String, Object> map);

    Clue getDetail(String id);

    List<Activity> getActivityByClueId(String id);

    int unbund(String id);

    List<Activity> searchActivity(Map<String, String> map);

    Clue getClueByClueId(String clueId);

    int delete(String clueId);
}
