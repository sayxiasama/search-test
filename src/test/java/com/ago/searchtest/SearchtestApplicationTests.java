package com.ago.searchtest;

import com.ago.opensearch.client.OpenSearchHandler;
import com.ago.searchtest.sync.service.ServeService;
import com.alibaba.fastjson.JSONObject;
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

        String search = openSearchHandler.search(config, sort, "valid:'0'", "id > '0'",null ,null,null );

        System.out.println(search);

    }

    @Test
    public void scrollSearch(){

        Config config = new Config().setHits(15);

        Sort sort = new Sort();
        sort.addToSortFields(new SortField("id", Order.INCREASE));

        String searchId = redisTemplate.opsForValue().get("scrollId");

        String search;
        if(StringUtils.isNotEmpty(searchId)){

            logger.info("cache searchId :{}",searchId);

            search = openSearchHandler.search(config, sort, "valid:'0'", "id > '0'",searchId,5,null);

            JSONObject parse = (JSONObject) JSONObject.parse(search);

            JSONObject result = (JSONObject)parse.get("result");

            String scrollId = (String) result.get("scroll_id");

            redisTemplate.opsForValue().set("scrollId",scrollId,5, TimeUnit.MINUTES);

            System.out.println("scrollId from cache : " + search);

        }else{

            String scrollId = openSearchHandler.createScrollId(config, sort, "valid:'0'", "id>'0'", null);

            logger.info("first request scrollId :{} ", scrollId);

            search = openSearchHandler.search(config, sort, "valid:'0'", "id > '0'",searchId,5,null);

            System.out.println("scrollId from request : " + search);

            redisTemplate.opsForValue().set("scrollId",scrollId,5, TimeUnit.MINUTES);
        }
    }

    @Test
    public void removeRedisScrollId(){

        String scrollId = redisTemplate.opsForValue().get("scrollId");

        logger.info("scrollId : {}",scrollId);

        redisTemplate.delete("scrollId");

    }


    @Test
    public void syncData(){

        serveService.syncDataToOpenSearch();

    }
}
