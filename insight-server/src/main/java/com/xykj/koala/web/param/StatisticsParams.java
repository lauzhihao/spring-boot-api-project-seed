package com.xykj.koala.web.param;

import lombok.Data;

/**
 * @author liuzhihao
 * @date 2018/4/17
 */
@Data
public class StatisticsParams {

    private String beginDate;

    private String endDate;

    private Long roleId;

    private Long targetStaffId;

    private Long provinceId;

    private Long cityId;

    private Long districtId;

    private Long schoolId;

    private Long classId;
}
