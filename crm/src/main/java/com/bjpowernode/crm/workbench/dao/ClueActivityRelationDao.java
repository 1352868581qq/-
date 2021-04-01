package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationDao {


    int bund(ClueActivityRelation car);

    List<Activity> SearchActivityByaname(String aname);

    List<ClueActivityRelation> getListByClueId(String clueId);

    int delete(String clueId);
}
