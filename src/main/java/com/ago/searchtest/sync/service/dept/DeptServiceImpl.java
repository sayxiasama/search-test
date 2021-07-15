package com.ago.searchtest.sync.service.dept;


import com.ago.opensearch.client.OpenSearchHandler;
import com.ago.opensearch.client.enums.OpenSearchOperateEnum;
import com.ago.opensearch.client.model.OpenSearchPushBody;
import com.ago.searchtest.sync.OpenSearchClientDemoUtil;
import com.ago.searchtest.sync.mapper.DeptMapper;
import com.ago.searchtest.sync.mapper.DeptMapper;
import com.ago.searchtest.sync.model.Company;
import com.ago.searchtest.sync.model.CompanyDept;
import com.ago.searchtest.sync.model.Serve;
import com.aliyun.opensearch.sdk.generated.commons.OpenSearchClientException;
import com.aliyun.opensearch.sdk.generated.commons.OpenSearchException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, CompanyDept> implements DeptService {

    private static final Logger logger = LoggerFactory.getLogger(DeptServiceImpl.class);

    private final DeptMapper DeptMapper;

    private final OpenSearchHandler openSearchHandler;

    public DeptServiceImpl(DeptMapper DeptMapper, OpenSearchHandler openSearchHandler) {
        this.DeptMapper = DeptMapper;
        this.openSearchHandler = openSearchHandler;
    }

    public void syncDataToOpenSearch(){

        List<CompanyDept> dept = DeptMapper.selectList(new QueryWrapper<CompanyDept>());

        if(!CollectionUtils.isEmpty(dept)){

            dept.stream().forEach( x -> {

                logger.info("current data info , {} " , x.toString());
                try {
                    openSearchHandler.objectPush(new OpenSearchPushBody(OpenSearchOperateEnum.ADD,x));
                } catch (OpenSearchClientException | OpenSearchException e) {
                    e.printStackTrace();
                }
                logger.info("sync current data end {} " , x.getId());
            });
        }
        logger.info("search total , {}", OpenSearchClientDemoUtil.search());

    }
}
