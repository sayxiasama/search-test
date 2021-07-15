package com.ago.searchtest.sync.mapper;

import com.ago.searchtest.sync.model.Company;
import com.ago.searchtest.sync.model.CompanyDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompanyMapper extends BaseMapper<Company> {
}
