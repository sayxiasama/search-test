package com.ago.searchtest;

import com.ago.opensearch.client.handler.OpenSearchHandler;
import com.ago.opensearch.client.model.request.OpenSearchParamInfo;
import com.ago.opensearch.client.model.request.ScrollSearchParams;
import com.ago.opensearch.client.model.request.SuggestionSearchParams;
import com.ago.opensearch.client.model.response.OpenSearchSearchResponse;
import com.ago.opensearch.client.model.response.SuggestionSearchResponse;
import com.ago.searchtest.sync.model.CompanyDTO;
import com.ago.searchtest.sync.model.LookOverReportDTO;
import com.ago.searchtest.sync.service.serve.ServeService;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.opensearch.sdk.generated.search.*;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Map<String,Order> sortFields = Maps.newHashMap();
        sortFields.put("report_id",Order.INCREASE);
        Map<String,String> queryFields = Maps.newHashMap();
        queryFields.put("company_id","41");
        String filterFields = null;
        filterFields = "report_id  < 0  AND valid = '0' AND report_rule_valid = '0' AND in(submit_status,\"0|3\") ";
        String fetchFields = "nick_name,company_id,icon_str,comment_num,thumb_num,submit_status,avatar,report_rule_title,submit_time,report_id,submit_end_time";
        OpenSearchParamInfo.OpenSearchParamInfoBuilder builder = OpenSearchParamInfo.simpleAttributeBuildInit(sortFields,queryFields,filterFields,fetchFields,0,10);

        Summary summary = getSummary("nick_name","50","em","...","10");
        builder.summary(summary);
//        Aggregate();
//        Distinct distinct = getDistinct();
//        builder.distinct(distinct);
//        String requestId = "162665916116800261130659";
//        builder.requestId(requestId);

        OpenSearchSearchResponse response = openSearchHandler.search(builder.build());
        System.out.println(response);

    }

    @Test
    public void v2(){

        String companyId = "41";
        String cursor = null;
        Integer pageSize = 10;
        Long userId = 41L;


        HashMap<String, Order> sortFields = Maps.newHashMap();
        sortFields.put("report_id",Order.DECREASE);

        HashMap<String, String> queryFields = Maps.newHashMap();
        queryFields.put("company_id",String.valueOf(companyId));

        StringBuilder baseFilter = new StringBuilder(" valid = '0' AND report_rule_valid = '0' AND in(submit_status,\"0|3\")");

        baseFilter.append(" AND user_id = '").append(userId+"'");

        StringBuilder pageable = new StringBuilder(baseFilter);
        if(Optional.ofNullable(cursor).isPresent()){
            pageable.append(" AND report_id < '").append(cursor+"'");
        }

        String filterFields = pageable.toString();
        String fetchFields = "nick_name,company_id,icon_str,comment_num,thumb_num,submit_status,avatar,report_rule_title,report_id,submit_end_time,receiver_ids,cc_people_ids";
        OpenSearchParamInfo.OpenSearchParamInfoBuilder builder = OpenSearchParamInfo.simpleAttributeBuildInit(sortFields,queryFields,filterFields,fetchFields,0,pageSize);
        OpenSearchSearchResponse search = openSearchHandler.search(builder.build());


        List<LookOverReportDTO> lookOverReportDTOS = JSONObject.parseArray(search.getResult().getItems(), LookOverReportDTO.class);

        Long lookOverReportDTO = lookOverReportDTOS.stream().min(Comparator.comparing(LookOverReportDTO::getReportId)).get().getReportId();

    }



    private Summary getSummary(String summaryKey , String len, String element ,String ellipsis, String snippet) {
        Summary summary = new Summary(summaryKey);
        summary.setSummary_len(len);//片段长度
        summary.setSummary_element(element); //飘红标签
        summary.setSummary_ellipsis(ellipsis);//片段链接符
        summary.setSummary_snippet(snippet);//片段数量
        return summary;
    }

    private Aggregate getAggregate(String key , String fun , String filter , String range , String aggSamplerThresHold , String samplerStep,String maxGroup) {
        Aggregate agg = new Aggregate();
        agg.setGroupKey("id"); //设置group_key
        agg.setAggFun("count()"); //设置agg_fun
        agg.setAggFilter("id=1"); //设置agg_filter
        agg.setRange("0~10"); //设置分段统计
        agg.setAggSamplerThresHold("5"); //设置采样阈值
        agg.setAggSamplerStep("5"); //设置采样步长
        agg.setMaxGroup("5"); //设置最大返回组数

        return agg;
    }

    private Distinct getDistinct(String key , Integer count,Integer times,Boolean reserved,Boolean updateTotalHit , String filter, String grade) {

        Distinct distinct = new Distinct();
        distinct.setKey("nick_name"); //设置dist_key
        distinct.setDistCount(1); //设置dist_count
        distinct.setDistTimes(1); //设置dist_times
        distinct.setReserved(false); //设置reserved
        distinct.setUpdateTotalHit(false); //设置update_total_hit
        distinct.setDistFilter("id<0"); //设置过滤条件
        distinct.setGrade("1.2"); //设置grade
        return distinct;
    }


    @Test
    public void suggestionRemind() throws UnsupportedEncodingException {

        SuggestionSearchParams suggestionSearchParams = SuggestionSearchParams.builder()
                .queryName("人")
                .hits(1)
                .userId("123456")
                .SuggestionName("user_or_company_hot").build();

        SuggestionSearchResponse suggestionSearchResponse = openSearchHandler.suggestionRemind(suggestionSearchParams);

        System.out.println(suggestionSearchResponse);


    }

    // deprecated
    @Test
    public void scrollSearch(){

        Config config = new Config().setHits(2);

//        Sort sort = getSort(null);

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
//        Sort sort = getSort(null);

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
    public void companyTest() throws UnsupportedEncodingException {

        Boolean openMultiSearch = false;

        if(!openMultiSearch){

            SuggestionSearchParams suggestionSearchParams = SuggestionSearchParams.builder()
                    .queryName("fen")
                    .hits(5)
                    .userId("123456")
                    .SuggestionName("company_search").build();

            SuggestionSearchResponse suggestionSearchResponse = openSearchHandler.suggestionRemind(suggestionSearchParams);


            return ;
        }

        defualtSearch();

    }

    private void defualtSearch() {
        String companyName = "分";
        HashMap<String, String> queryFields = Maps.newHashMap();
        // 是否存在必要的query子句
        Boolean hasQueryRequire = StringUtils.isNotEmpty(companyName);


        if(hasQueryRequire){
            queryFields.put("name",companyName);
        }else{
            queryFields.put("valid","0");
        }

        HashMap<String, Order> sortFields = Maps.newHashMap();
        sortFields.put("company_id",Order.DECREASE);
        String fetchFields = "company_id,area_code,city_code,valid,name,city_name,area_name";

        OpenSearchParamInfo.OpenSearchParamInfoBuilder simpleBuild = OpenSearchParamInfo.simpleAttributeBuildInit(sortFields, queryFields, hasQueryRequire ? "valid = '0'" : null, fetchFields, 0,10);

        OpenSearchSearchResponse search = openSearchHandler.search(simpleBuild.build());

        List<CompanyDTO> companyDTOS = JSONObject.parseArray(search.getResult().getItems(), CompanyDTO.class);

        companyDTOS.stream().peek( x -> {

            x.setPosition(x.getCityName() + "-" + x.getAreaName());

        }).forEach(System.out::println);


    }


    @Test
    public void syncData(){

        serveService.syncDataToOpenSearch();

    }
}
