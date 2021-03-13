package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.settings.dao.DicTypeDao;
import com.bjpowernode.crm.settings.dao.DicValueDao;
import com.bjpowernode.crm.settings.domain.DicType;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {
    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);
    @Override
    public Map<String, List<DicValue>> getAll() {
        Map<String,List<DicValue>> map = new HashMap<>();
        //查询字典的所有类型
        List<DicType> dicTypelist= dicTypeDao.getDicTypelist();
        //查询每种类型的值。
        for(DicType dtlist : dicTypelist){
            String code = dtlist.getCode();
            List<DicValue> dvlist = dicValueDao.getDicValuelist(code);
            map.put(code+"list",dvlist);
        }
        return map;
    }
}
