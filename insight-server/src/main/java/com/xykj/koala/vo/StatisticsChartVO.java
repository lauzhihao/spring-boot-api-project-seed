package com.xykj.koala.vo;

import lombok.Data;

import java.util.function.BinaryOperator;

/**
 * @author liuzhihao
 * @date 2018/4/19
 */
@Data
public class StatisticsChartVO {

    private Integer range1;

    private String range1Label;

    private Integer range2;

    private String range2Label;

    private Integer range3;

    private String range3Label;

    private Integer range4;

    private String range4Label;

    private Integer range5;

    private String range5Label;

    private Long classId;

    private Long schoolId;

    private Long districtId;

    public static StatisticsChartVO createDefault() {

        return new StatisticsChartVO();
    }

    public static final BinaryOperator<StatisticsChartVO> CHART_SUM_OPERATOR = (x, y) -> {
        StatisticsChartVO chartVO = StatisticsChartVO.createDefault();
        chartVO.setRange1(x.getRange1() + y.getRange1());
        chartVO.setRange2(x.getRange2() + y.getRange2());
        chartVO.setRange4(x.getRange4() + y.getRange4());
        chartVO.setRange5(x.getRange5() + y.getRange5());
        chartVO.setRange3(x.getRange3() + y.getRange3());

        return chartVO;
    };

    public String getRange1Label(){
        return "0";
    }

    public String getRange2Label(){
        return "1";
    }

    public String getRange3Label(){
        return "2";
    }

    public String getRange4Label(){
        return "3";
    }

    public String getRange5Label(){
        return "> 3";
    }
}