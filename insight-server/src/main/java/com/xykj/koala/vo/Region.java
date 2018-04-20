package com.xykj.koala.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liuzhihao
 * @date 2018/4/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Region {

    private Long countryId;

    private Long provinceId;

    private Long cityId;

    private Long districtId;

    private String abbreviation;

    public static Region createDefault() {

        return new Region(0L, 0L, 0L, 0L, "");
    }


}
