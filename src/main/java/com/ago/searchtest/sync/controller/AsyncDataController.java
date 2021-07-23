package com.ago.searchtest.sync.controller;


import com.ago.searchtest.sync.config.AjaxResult;
import com.ago.searchtest.sync.config.BusinessException;
import com.ago.searchtest.sync.service.company.CompanyService;
import com.ago.searchtest.sync.service.dept.DeptService;
import com.ago.searchtest.sync.service.serve.ServeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/")
public class AsyncDataController {


    private final ServeService ServeService;

    private final DeptService deptService;

    private final CompanyService companyService;

    public AsyncDataController(ServeService ServeService, DeptService deptService, CompanyService companyService) {
        this.ServeService = ServeService;
        this.deptService = deptService;
        this.companyService = companyService;
    }


    @GetMapping("/asyncData")
    public ResponseEntity<Object> asyncData(){

//        ServeService.syncDataToOpenSearch();
        new Thread(()->{
            deptService.syncDataToOpenSearch();
        }).start();

//        new Thread(()->{
//            companyService.syncDataToOpenSearch();
//        }).start();

        return ResponseEntity.ok().build();
    }


    @GetMapping("/globaException")
    public AjaxResult test(String userId , String phone , Boolean printException){

        System.out.println(userId +":"+phone);
        if(printException){
            throw new BusinessException("测试异常捕获");
        }
        List<Map<String,Object>> list = Lists.newArrayList();
        HashMap<String, Object> clause = Maps.newHashMap();
        clause.put("three",AjaxResult.failed(null));


        HashMap<String, Object> clause2 = Maps.newHashMap();
        clause2.put("one",AjaxResult.ok("测试返回"));

        list.add(clause);
        list.add(clause2);

        return AjaxResult.ok(list);
    }


    public static void main(String[] args) {
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);

        System.out.println(integers.stream().min(Integer::compareTo).get());


    }

}
