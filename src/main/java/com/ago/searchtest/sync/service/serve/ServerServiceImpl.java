package com.ago.searchtest.sync.service.serve;


import com.ago.opensearch.client.enums.OpenSearchOperateEnum;
import com.ago.opensearch.client.handler.OpenSearchHandler;
import com.ago.opensearch.client.model.request.OpenSearchPushBody;
import com.ago.searchtest.sync.OpenSearchClientDemoUtil;
import com.ago.searchtest.sync.mapper.ServerMapper;
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
public class ServerServiceImpl extends ServiceImpl<ServerMapper, Serve> implements ServeService{

    private static final Logger logger = LoggerFactory.getLogger(ServerServiceImpl.class);

    private final ServerMapper serverMapper;

    private final OpenSearchHandler openSearchHandler;

    public ServerServiceImpl(ServerMapper serverMapper, OpenSearchHandler openSearchHandler) {
        this.serverMapper = serverMapper;
        this.openSearchHandler = openSearchHandler;
    }


    public void syncDataToOpenSearch(){

        List<Serve> serves = serverMapper.selectList(new QueryWrapper<Serve>());

        if(!CollectionUtils.isEmpty(serves)){

            serves.stream().forEach( x -> {

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
