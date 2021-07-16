package com.ago.searchtest.sync.model;


import com.ago.opensearch.client.model.OpenSearchModel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@TableName("tb_serve")
public class Serve extends OpenSearchModel implements Serializable {

    @TableId
    private Long id;
    /**
     * 公司ID
     */
    @TableField
    private Long company_id;
    /**
     * 类别
     */
    @TableField
    private String serve_category_code;
    /**
     * 缩略图
     */
    @TableField
    private String thumbnail;
    /**
     * 标题
     */
    @TableField
    private String title;
    /**
     * 价格
     */
    @TableField
    private String price;
    /**
     * 服务范围
     */
    @TableField
    private String serve_range;
    /**
     * 综合评分
     */
    @TableField
    private String sum_score;
    /**
     * 咨询量
     */
    @TableField
    private String advisory_num;
    /**
     * 评论量
     */
    @TableField
    private String comment_num;
    /**
     * 是否上架标记（'0'.下架,'1'.上架）
     */
    @TableField
    private String publish_status;
    /**
     * 创建时间
     */
    @TableField
    private Date create_time;
    /**
     * 是否有效(0.有效,1.失效)
     */
    @TableField
    private String valid;
}

