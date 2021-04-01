package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Customer;
import org.apache.ibatis.annotations.Param;

import javax.naming.Name;
import java.util.List;

public interface CustomerDao {

    Customer getCustomerByName(String company);

    int save(Customer customer);

    List<String> getCustomerName(String name);

    String getCustomerIdName(String customerName);
}
