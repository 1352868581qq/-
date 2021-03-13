package com.bjpowernode.crm.workbench.service.imlp;

import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.dao.ClueActivityRelationDao;
import com.bjpowernode.crm.workbench.dao.ClueDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.ClueActivityRelation;
import com.bjpowernode.crm.workbench.service.ClueService;

import javax.xml.stream.FactoryConfigurationError;
import java.util.List;
import java.util.Map;

public class ClueServiceImmpl implements ClueService {
    private ClueDao clueDao= SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao= SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
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
}
