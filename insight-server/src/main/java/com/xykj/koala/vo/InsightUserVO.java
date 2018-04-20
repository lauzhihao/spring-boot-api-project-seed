package com.xykj.koala.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author liuzhihao
 * @date 2018/4/11
 */
@Data
@Builder
public class InsightUserVO {

    private Long userId;

    private LocalDateTime createTime;
}
