package com.ago.searchtest.Test;

import com.ago.opensearch.client.OpenSearchHandler;
import com.aliyun.opensearch.sdk.generated.search.Config;
import com.aliyun.opensearch.sdk.generated.search.Order;
import com.aliyun.opensearch.sdk.generated.search.Sort;
import com.aliyun.opensearch.sdk.generated.search.SortField;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {


    private final OpenSearchHandler openSearchHandler;


    public Test(OpenSearchHandler openSearchHandler) {
        this.openSearchHandler = openSearchHandler;
    }


    @GetMapping(value = "/starter-res")
    public String starter(){

        return openSearchHandler.testAutoConfiguration();
    }


    @GetMapping(value = "/open-search")
    public String opensearch(){

        Config config = new Config().setStart(0).setHits(5);

        Sort sort = new Sort();
        sort.addToSortFields(new SortField("id", Order.INCREASE));

        String search = openSearchHandler.search(config, sort, "serve_category_code:'01'", "id > '0'",null,null,null );

        return search;
    }
}
