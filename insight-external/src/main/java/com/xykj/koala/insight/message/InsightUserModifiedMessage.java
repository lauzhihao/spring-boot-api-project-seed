package com.xykj.koala.insight.message;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liuzhihao
 * @date 2018/4/11
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class InsightUserModifiedMessage extends AbstractInsightMessage {

    private Long userId;

    private Long updateTime;

}
