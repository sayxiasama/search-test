package com.ago.searchtest;

import com.ago.opensearch.client.handler.OpenSearchHandler;
import com.ago.opensearch.client.model.request.OpenSearchParamInfo;
import com.ago.opensearch.client.model.request.ScrollSearchParams;
import com.ago.opensearch.client.model.response.OpenSearchSearchResponse;
import com.ago.searchtest.sync.service.serve.ServeService;
import com.aliyun.opensearch.sdk.generated.search.*;
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
    private OpenSearchHandler openSearchHandler;

    @Autowired
    private ServeService serveService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void search(){

        Config config = new Config().setStart(0).setHits(5);

        Sort sort = new Sort();
        sort.addToSortFields(new SortField("id", Order.INCREASE));

        String query = "default:'人'";
        String filter= "id > '0'";
        String queryOriginalContent ="人";

        OpenSearchParamInfo searchParamInfo = OpenSearchParamInfo.builder().config(config)
                .sort(sort)
                .filter(filter)
                .query(query)
                .originalQueryContent(queryOriginalContent)
                .build();

        Distinct distinct = new Distinct();
        distinct.setKey("dept_name"); //设置dist_key
        distinct.setDistCount(1); //设置dist_count
        distinct.setDistTimes(1); //设置dist_times
        distinct.setReserved(false); //设置reserved
        distinct.setUpdateTotalHit(false); //设置update_total_hit
        distinct.setDistFilter("id<0"); //设置过滤条件
        distinct.setGrade("1.2"); //设置grade

        searchParamInfo.setDistinct(distinct);

        Aggregate agg = new Aggregate();
        agg.setGroupKey("id"); //设置group_key
        agg.setAggFun("count()"); //设置agg_fun
        agg.setAggFilter("id=1"); //设置agg_filter
        agg.setRange("0~10"); //设置分段统计
        agg.setAggSamplerThresHold("5"); //设置采样阈值
        agg.setAggSamplerStep("5"); //设置采样步长
        agg.setMaxGroup("5"); //设置最大返回组数

        Summary summary = new Summary("dept_name");

        summary.setSummary_len("50");//片段长度
        summary.setSummary_element("em"); //飘红标签
        summary.setSummary_ellipsis("...");//片段链接符
        summary.setSummary_snippet("1");//片段数量

        searchParamInfo.setSummary(summary);

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

            OpenSearchParamInfo openSearchParamInfo =  OpenSearchParamInfo.builder()
                    .config(config)
                    .query(query)
                    .filter(filter)
                    .scrollId(searchId)
                    .scrollIdExpire(3).build();
            OpenSearchSearchResponse response = openSearchHandler.search(openSearchParamInfo);

            redisTemplate.opsForValue().set("scrollId",response.getResult().getScrollId(),3, TimeUnit.MINUTES);

            System.out.println("scrollId from cache : " + response.getResult().getScrollId());

        }else{

           ScrollSearchParams openSearchGetScrollParams = new ScrollSearchParams(config, query, filter);

            String scrollId = openSearchHandler.createScrollId(openSearchGetScrollParams);

            logger.info("first request scrollId :{} ", scrollId);

            OpenSearchParamInfo openSearchParamInfo =  OpenSearchParamInfo.builder()
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

        ScrollSearchParams openSearchGetScrollParams = new ScrollSearchParams(config, query, filter);


        String scrollId = openSearchHandler.createScrollId(openSearchGetScrollParams);

        logger.info("first scrollId : {} " , scrollId);


        OpenSearchParamInfo openSearchParamInfo =  OpenSearchParamInfo.builder()
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
