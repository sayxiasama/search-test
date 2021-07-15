package com.ago.searchtest.sync.model;


import com.ago.opensearch.client.OpenSearchModel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@TableName("tb_dept")
public class CompanyDept extends OpenSearchModel {

    @TableId
    private Long id;

    @TableField
    private Long company_id;
    @TableField
    private Long first_dept_id;
    @TableField
    private Long parent_id;
    @TableField
    private String dept_name;
    @TableField
    private String dept_duty;
    @TableField
    private int order_num;

    @TableField
    private int leader;
    @TableField
    private Long create_by;

    @TableField
    private String create_time;

    @TableField
    private String valid;

    @TableField
    private String remark;

    @TableField
    private String flag;
}

