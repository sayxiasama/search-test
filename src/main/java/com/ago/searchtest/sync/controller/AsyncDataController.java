package com.ago.searchtest.sync.controller;


import com.ago.searchtest.sync.service.company.CompanyService;
import com.ago.searchtest.sync.service.dept.DeptService;
import com.ago.searchtest.sync.service.serve.ServeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
