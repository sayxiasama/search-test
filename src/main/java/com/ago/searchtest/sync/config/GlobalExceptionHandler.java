package com.ago.searchtest.sync.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(BusinessException.class)
    public AjaxResult<Object> businessExceptionHandler(HttpServletRequest request, BusinessException e){

        logger.info("request param [{}] " , request.getParameterMap().toString());

        return AjaxResult.failed(e.getMessage());
    }

}