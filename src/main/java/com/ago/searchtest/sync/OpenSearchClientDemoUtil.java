package com.ago.searchtest.sync;

import com.ago.opensearch.client.model.OpenSearchModel;
import com.ago.searchtest.sync.model.Serve;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.opensearch.DocumentClient;
import com.aliyun.opensearch.OpenSearchClient;
import com.aliyun.opensearch.SearcherClient;
import com.aliyun.opensearch.sdk.dependencies.com.google.common.collect.Lists;
import com.aliyun.opensearch.sdk.generated.OpenSearch;
import com.aliyun.opensearch.sdk.generated.commons.OpenSearchClientException;
import com.aliyun.opensearch.sdk.generated.commons.OpenSearchException;
import com.aliyun.opensearch.sdk.generated.commons.OpenSearchResult;
import com.aliyun.opensearch.sdk.generated.search.*;
import com.aliyun.opensearch.sdk.generated.search.general.SearchResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class OpenSearchClientDemoUtil {

    private static final Logger logger = LoggerFactory.getLogger(OpenSearchClientDemoUtil.class);

    /**
     * 集成时 需要搜索服务的应用自行配置
     */
    private static String appName;
    private static String accessKey;
    private static String secret;
    private static String host;
    private static String tableName;

    //获取openSearch连接
    public static OpenSearchClient getClient(){

        OpenSearch openSearch = new OpenSearch(accessKey, secret, host);

        OpenSearchClient openSearchClient = new OpenSearchClient(openSearch);

        return openSearchClient;
    }


    public static void objectPush(OpenSearchPushBody openSearchPushBody) throws OpenSearchClientException, OpenSearchException {

        DocumentClient documentClient = new DocumentClient(getClient());

        ArrayList<OpenSearchPushBody> param = Lists.newArrayList();

        if(Optional.ofNullable(openSearchPushBody).isPresent()){
            logger.info(" push start , push body {} " , JSONObject.toJSONString(openSearchPushBody));
            param.add(openSearchPushBody);
            OpenSearchResult push = documentClient.push(JSONObject.toJSONString(param), appName, tableName);
            logger.info(" push end , result info {} " , push.toString());
            return;
        }
        OpenSearchPushBody singlePush = new OpenSearchPushBody(OpenSearchOperateEnum.ADD, Serve.builder().id(9999L).title("test-manual-push").build());
        logger.info("single push start , push body {} " , JSONObject.toJSONString(singlePush));
        param.add(singlePush);
        logger.info("json array {}" , JSONObject.toJSONString(param));
        OpenSearchResult push = documentClient.push(JSONObject.toJSONString(param), appName, tableName);
        logger.info("single push end , result info {} " , push.toString());
    }

    public static void attributePush(HashMap<String, Object> pushParams) throws OpenSearchClientException, OpenSearchException {

        DocumentClient documentClient = new DocumentClient(getClient());

        documentClient.add(pushParams);

        Optional optional = Optional.ofNullable(documentClient.commit(appName, tableName));

        if(!optional.isPresent()){
            logger.info("push error , application process error , result empty ");
        }

        OpenSearchResult openSearchResult = (OpenSearchResult)optional.get();

        if(!openSearchResult.getResult().equalsIgnoreCase("true")){
            logger.info("push error , result status false");
        }

        logger.info("batch push result : {}", optional.get());
    }

    public static void objectUpdate(OpenSearchPushBody openSearchPushBody) throws OpenSearchClientException, OpenSearchException {

        DocumentClient documentClient = new DocumentClient(getClient());

        ArrayList<OpenSearchPushBody> param = Lists.newArrayList();

        if(Optional.ofNullable(openSearchPushBody).isPresent()){
            logger.info(" push start , push body {} " , JSONObject.toJSONString(openSearchPushBody));
            param.add(openSearchPushBody);
            OpenSearchResult push = documentClient.push(JSONObject.toJSONString(param), appName, tableName);
            logger.info(" push end , result info {} " , push.toString());
            return;
        }
        OpenSearchPushBody singlePush = new OpenSearchPushBody(OpenSearchOperateEnum.ADD, Serve.builder().id(8888L).title("test-manual-update").build());
        logger.info("single push start , push body {} " , JSONObject.toJSONString(singlePush));
        param.add(singlePush);
        logger.info("json array {}" , JSONObject.toJSONString(param));
        OpenSearchResult push = documentClient.push(JSONObject.toJSONString(param), appName, tableName);
        logger.info("single push end , result info {} " , push.toString());
    }

    public static String search(){

        final OpenSearchClient openSearchClient = getClient();

        final SearcherClient searcherClient = new SearcherClient(openSearchClient);

        Config config = new Config(Lists.newArrayList(appName));

//        config.setStart(0);
//
//        config.setHits(5);

        config.setSearchFormat(SearchFormat.JSON);

        final SearchParams searchParams = new SearchParams(config);

        searchParams.setQuery("id:'84' ");
//        searchParams.setFilter("id>'0'");
//        searchParams.setFilter("name != 'hello-search'");

        //聚合参数(类比 sql group By)
//        HashSet<Aggregate> aggregates = Sets.newHashSet();
//
//        aggregates.add(new Aggregate().setGroupKey("id"));
//
//        searchParams.setAggregates(aggregates);

        Sort sort = new Sort(); //排序字段必须为索引字段

        sort.addToSortFields(new SortField("id",Order.DECREASE));

//        sort.addToSortFields(new SortField("name",Order.INCREASE));

        searchParams.setSort(sort);

        SearchResult execute = null;
        try {
             execute = searcherClient.execute(searchParams);
        } catch (OpenSearchException e) {
            e.printStackTrace();
        } catch (OpenSearchClientException e) {
            e.printStackTrace();
        }

        final Optional<SearchResult> option = Optional.ofNullable(execute);
        if(option.isPresent()){
            logger.info("query result {}", execute);
            return option.get().getResult();
        }

        return "query is empty ";

    }

    public static void attributeRemove(HashMap<String, Object> pushParams) throws OpenSearchClientException, OpenSearchException {

        DocumentClient documentClient = new DocumentClient(getClient());

        documentClient.add(pushParams);

        documentClient.remove(pushParams);

        OpenSearchResult commit = documentClient.commit(appName, tableName);

        logger.info("remove commit info {} " , commit.toString());

    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OpenSearchPushBody<T extends OpenSearchModel>{
        private OpenSearchOperateEnum cmd;
        private T fields;
    }

    public enum OpenSearchOperateEnum{
        ADD,REMOVE;
    }
}
