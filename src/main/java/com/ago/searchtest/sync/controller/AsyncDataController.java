package com.ago.searchtest.sync.controller;


import com.ago.searchtest.sync.service.ServeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AsyncDataController {


    private final ServeService ServeService;

    public AsyncDataController(ServeService ServeService) {
        this.ServeService = ServeService;
    }


    @GetMapping("/asyncData")
    public ResponseEntity<Object> asyncData(){


        ServeService.syncDataToOpenSearch();

        return ResponseEntity.ok().build();
    }

}
