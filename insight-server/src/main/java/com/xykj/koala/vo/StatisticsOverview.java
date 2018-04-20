package com.xykj.koala.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author liuzhihao
 * @date 2018/4/19
 */

@Data
@Builder
public class StatisticsOverview {

    private Integer totalQualified;

    private Integer totalLastMonth;

    private Integer totalJoined;

    private String totalRatio;

}