package com.xykj.koala.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author liuzhihao
 */
@Data
@Table(name = "insight_staff_region")
public class InsightStaffRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "staff_id")
    private Long staffId;

    @Column(name = "country_id")
    private Long countryId;

    @Column(name = "province_id")
    private Long provinceId;

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "create_time")
    private Date createTime;

}