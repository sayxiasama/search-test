package com.ago.searchtest.sync.service.company;


import com.ago.searchtest.sync.model.Company;
import com.ago.searchtest.sync.model.CompanyDept;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CompanyService extends IService<Company> {

    void syncDataToOpenSearch();
}
