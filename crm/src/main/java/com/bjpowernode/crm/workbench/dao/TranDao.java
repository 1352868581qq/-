package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranDao {

    int save(Tran tran);

    List<Tran> getTranPageList(Map<String,Object> map);

    int getTotal(Map<String, Object> map);

    Tran getDetail(String id);

    int changeStage(Tran tran);

    int getTranTotal();

    List<Map<String, Object>> getChart();
}
