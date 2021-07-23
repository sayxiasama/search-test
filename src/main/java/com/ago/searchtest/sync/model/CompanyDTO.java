package com.ago.searchtest.sync.model;

import lombok.Data;

import java.io.Serializable;


@Data
public class CompanyDTO implements Serializable {

    private String cityName;

    private String areaName;

    private String valid;

    private String companyId;

    private String areaCode;

    private String name;
    private String cityCode;

    private String position;

}
