package com.xykj.koala.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author liuzhihao
 * @date 2018/4/13
 */
@Data
@Builder
public class InsightUserClassVO {

    private Long userId;

    private Long classId;

    private Date createTime;
}
