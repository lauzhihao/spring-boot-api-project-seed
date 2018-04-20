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
public class InsightUserTaskMessage extends AbstractInsightMessage {

    private Long userId;

    private Long createTime;

    private Integer erScore;

    //InitExam: erStudentTaskService.studentSubmitERTest
    //BookExam: extraBookTaskService.submitExam
    //StepExam: stepTaskService.studentSubmitExam
}
