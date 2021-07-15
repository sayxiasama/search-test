package com.ago.searchtest;

import com.ago.opensearch.client.OpenSearchHandler;
import com.ago.opensearch.client.model.OpenSearchGetScrollParams;
import com.ago.opensearch.client.model.OpenSearchParamInfo;
import com.ago.opensearch.client.model.OpenSearchSearchResponse;
import com.ago.searchtest.sync.service.serve.ServeService;
import com.aliyun.opensearch.sdk.generated.search.Config;
import com.aliyun.opensearch.sdk.generated.search.Order;
import com.aliyun.opensearch.sdk.generated.search.Sort;
import com.aliyun.opensearch.sdk.generated.search.SortField;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
class SearchtestApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(SearchtestApplicationTests.class);

    @Test
    void contextLoads() {
    }
    @Autowired
    private  OpenSearchHandler openSearchHandler;

    @Autowired
    private ServeService serveService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void search(){

        Config config = new Config().setStart(0).setHits(5);

        Sort sort = new Sort();
        sort.addToSortFields(new SortField("id", Order.INCREASE));

        String query = "default:'平面'";
        String filter= "id > '0'";

        OpenSearchParamInfo searchParamInfo = new OpenSearchParamInfo.OpenSearchParamInfoBuilder().config(config)
                .sort(sort)
                .filter(filter)
                .query(query).build();

        OpenSearchSearchResponse response = openSearchHandler.search(searchParamInfo);

        System.out.println(response);

    }

    @Test
    public void scrollSearch(){

        Config config = new Config().setHits(2);

        Sort sort = new Sort();
        sort.addToSortFields(new SortField("id", Order.INCREASE));

        String searchId = redisTemplate.opsForValue().get("scrollId");

        String query = "valid:'0'";
        String filter= "id > '0'";

        if(StringUtils.isNotEmpty(searchId)){

            logger.info("cache searchId :{}",searchId);

            OpenSearchParamInfo openSearchParamInfo = new OpenSearchParamInfo.OpenSearchParamInfoBuilder()
                    .config(config)
                    .query(query)
                    .filter(filter)
                    .scrollId(searchId)
                    .scrollIdExpire(3).build();
            OpenSearchSearchResponse response = openSearchHandler.search(openSearchParamInfo);

            redisTemplate.opsForValue().set("scrollId",response.getResult().getScrollId(),3, TimeUnit.MINUTES);

            System.out.println("scrollId from cache : " + response.getResult().getScrollId());

        }else{

            OpenSearchGetScrollParams openSearchGetScrollParams = new OpenSearchGetScrollParams(config, query, filter);

            String scrollId = openSearchHandler.createScrollId(openSearchGetScrollParams);

            logger.info("first request scrollId :{} ", scrollId);

            OpenSearchParamInfo openSearchParamInfo = new OpenSearchParamInfo.OpenSearchParamInfoBuilder()
                    .config(config)
                    .query(query)
                    .filter(filter)
                    .scrollId(scrollId)
                    .scrollIdExpire(3).build();

            OpenSearchSearchResponse response = openSearchHandler.search(openSearchParamInfo);

            System.out.println("scrollId from request : " + response);

            redisTemplate.opsForValue().set("scrollId",scrollId,3, TimeUnit.MINUTES);
        }
    }

    @Test
    public void removeRedisScrollId(){

        String scrollId = redisTemplate.opsForValue().get("scrollId");

        logger.info("scrollId : {}",scrollId);

        redisTemplate.delete("scrollId");

    }

    @Test
    public void scrollSearchTwice(){

        Config config = new Config().setHits(2);

//        Sort sort = new Sort(Arrays.asList(new SortField("id",Order.INCREASE),new SortField("RANK", Order.INCREASE)));
        Sort sort = new Sort();
        sort.addToSortFields(new SortField("id",Order.INCREASE));

        String query = "valid:'0'";
        String filter= "id>'0'";

        OpenSearchGetScrollParams openSearchGetScrollParams = new OpenSearchGetScrollParams(config, query, filter);


        String scrollId = openSearchHandler.createScrollId(openSearchGetScrollParams);

        logger.info("first scrollId : {} " , scrollId);


        OpenSearchParamInfo openSearchParamInfo = new OpenSearchParamInfo.OpenSearchParamInfoBuilder()
                .config(config)
                .query(query)
                .filter(filter)
                .scrollId(scrollId)
                .scrollIdExpire(3).build();

        OpenSearchSearchResponse response = openSearchHandler.search(openSearchParamInfo);

        logger.info("response {} ", response);

        int i = 0;

        while(i < 2){

            openSearchParamInfo.setScrollId(response.getResult().getScrollId());

            response = openSearchHandler.search(openSearchParamInfo);

            logger.info("twice search {} " , response);

            i++;
        }

    }


    @Test
    public void syncData(){

        serveService.syncDataToOpenSearch();

    }
}
