package com.xykj.koala.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author liuzhihao
 * @date 2018/4/15
 */
@Data
public final class RegionVO {

    @JSONField(name = "regionId")
    private Long id;

    @JSONField(serialize = false)
    private String regionCode;

    private String regionName;

    @JSONField(serialize = false)
    private Long parentId;

    @JSONField(serialize = false)
    private String parentCode;

    @JSONField(serialize = false)
    private String abbreviation;

    public static final String COUNTRY_CODE = "000000";

    public static RegionVO createDefault() {
        RegionVO regionVO = new RegionVO();
        regionVO.setId(0L);
        regionVO.setRegionName("全部");
        return regionVO;
    }

}
