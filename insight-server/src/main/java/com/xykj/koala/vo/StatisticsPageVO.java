package com.xykj.koala.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author liuzhihao
 * @date 2018/4/19
 */
@Data
@Builder
public class StatisticsPageVO {

    private StatisticsOverview overview;

    private List<StatisticsResultVO> list;

    private List<InsightUserRoleVO> roles;

    private StatisticsChartVO chart;

    private List<OperationLogVO> logs;
}

