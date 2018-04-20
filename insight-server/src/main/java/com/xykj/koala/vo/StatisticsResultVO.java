package com.xykj.koala.vo;

import lombok.Data;

import java.util.function.BinaryOperator;

/**
 * @author liuzhihao
 * @date 2018/4/18
 */
@Data
public class StatisticsResultVO {

    private Long provinceId;

    private String provinceName;

    private Long cityId;

    private String cityName;

    private Long districtId;

    private String districtName;

    private Long schoolId;

    private String schoolName;

    private Long classId;

    private String className;

    private String teacherName;

    private Long staffId;

    private String employeeName;

    private Long roleId;

    private Integer totalQualified;

    private Integer totalActual;

    private Integer totalJoined;

    private String totalRatio;

    public static StatisticsResultVO createDefault() {
        StatisticsResultVO statisticsResultVO = new StatisticsResultVO();
        statisticsResultVO.setTotalQualified(0);
        statisticsResultVO.setTotalJoined(0);
        statisticsResultVO.setTotalActual(0);
        return statisticsResultVO;
    }

    public static final BinaryOperator<StatisticsResultVO> SUM_REDUCER = (x, y) -> {
        StatisticsResultVO r = new StatisticsResultVO();
        r.setTotalActual(x.getTotalActual() + y.getTotalActual());
        r.setTotalJoined(x.getTotalJoined() + y.getTotalJoined());
        r.setTotalQualified(x.getTotalQualified() + y.getTotalQualified());
        return r;
    };
}
