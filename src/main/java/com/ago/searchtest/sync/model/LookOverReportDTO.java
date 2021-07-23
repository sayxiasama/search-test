package com.ago.searchtest.sync.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LookOverReportDTO implements Serializable {


    private String avatar;

    private String iconStr;

    private String nickName;

    private String reportRuleTitle;

    private String thumbNum;

    private String submitStatus;

    private String commentNum;

    private Long reportId;

    private String receiverIds;

    private String receiverNickNameStr;

    private String ccPeopleIds;

    private String ccPeopleNickNameStr;

    private Timestamp submitEndTime;

    private String submitEndTimeStr;

}
