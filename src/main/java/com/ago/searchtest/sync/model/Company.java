package com.ago.searchtest.sync.model;

import com.ago.opensearch.client.model.OpenSearchModel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("tb_company")
public class Company extends OpenSearchModel {

    @TableId
    private Long id;
    @TableField
    private String name;
    @TableField
    private String logo;
    @TableField
    private String address;
    @TableField
    private String synopsis;
    @TableField
    private String scala_code;
    @TableField
    private String province_code;
    @TableField
    private String city_code;
    @TableField
    private String area_code;
    @TableField
    private String longitude;
    @TableField
    private String latitude;
    @TableField
    private String is_clock_in;
    @TableField
    private String start_work_time;
    @TableField
    private String end_work_time;
    @TableField
    private Long create_by;
    @TableField
    private Date create_time;
    @TableField
    private String valid;
    @TableField
    private String remark;
}

