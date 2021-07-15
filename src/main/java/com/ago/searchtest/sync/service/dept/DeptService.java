package com.ago.searchtest.sync.service.dept;


import com.ago.searchtest.sync.model.CompanyDept;
import com.ago.searchtest.sync.model.Serve;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DeptService extends IService<CompanyDept> {

    void syncDataToOpenSearch();
}
