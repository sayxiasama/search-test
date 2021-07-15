package com.ago.searchtest.sync.service.serve;


import com.ago.searchtest.sync.model.Serve;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ServeService extends IService<Serve> {

    void syncDataToOpenSearch();
}
