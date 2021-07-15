package com.ago.searchtest.sync.service.company;


import com.ago.opensearch.client.OpenSearchHandler;
import com.ago.opensearch.client.enums.OpenSearchOperateEnum;
import com.ago.opensearch.client.model.OpenSearchPushBody;
import com.ago.searchtest.sync.OpenSearchClientDemoUtil;
import com.ago.searchtest.sync.mapper.CompanyMapper;
import com.ago.searchtest.sync.model.Company;
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
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyServiceImpl.class);

    private final CompanyMapper CompanyMapper;

    private final OpenSearchHandler openSearchHandler;

    public CompanyServiceImpl(CompanyMapper CompanyMapper, OpenSearchHandler openSearchHandler) {
        this.CompanyMapper = CompanyMapper;
        this.openSearchHandler = openSearchHandler;
    }

    public void syncDataToOpenSearch(){

        List<Company> Company = CompanyMapper.selectList(new QueryWrapper<Company>());

        if(!CollectionUtils.isEmpty(Company)){

            Company.stream().forEach( x -> {

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
