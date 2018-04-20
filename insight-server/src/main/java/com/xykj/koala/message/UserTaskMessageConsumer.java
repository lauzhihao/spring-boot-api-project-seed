package com.xykj.koala.message;

import com.xykj.koala.enums.StudentTaskTypeEnum;
import com.xykj.koala.insight.message.InsightMessageTypeEnum;
import com.xykj.koala.insight.message.InsightUserTaskMessage;
import com.xykj.koala.insight.util.JsonUtils;
import com.xykj.koala.model.KoalaStudentTaskRecord;
import com.xykj.koala.service.KoalaStudentTaskRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Random;

import static com.xykj.koala.insight.common.InsightMessageConstants.*;

/**
 * @author liuzhihao
 * @date 2018/4/14
 */
//@Component
@Slf4j
public class UserTaskMessageConsumer {

    @Resource
    private KoalaStudentTaskRecordService koalaStudentTaskRecordService;

    @RabbitListener(queues = {INSIGHT_BOOK_TASK_QUEUE})
    @Async
    public void consumeBookTaskMessage(Message message) {
        this.saveTaskRecord(message, StudentTaskTypeEnum.BOOK_TASK);
    }

    @RabbitListener(queues = {INSIGHT_STEP_TASK_QUEUE})
    @Async
    public void consumeStepTaskMessage(Message message) {
        this.saveTaskRecord(message, StudentTaskTypeEnum.STEP_TASK);
    }

    @RabbitListener(queues = {INSIGHT_ER_TEST_QUEUE})
    @Async
    public void consumeERTaskMessage(Message message) {
        this.saveTaskRecord(message, StudentTaskTypeEnum.ER_TASK);
    }

    private void saveTaskRecord(Message message, StudentTaskTypeEnum taskType) {
        try {
            String msg = new String(message.getBody());
            log.debug(msg);
            JsonUtils.fromJson(msg, InsightUserTaskMessage.class)
                    .ifPresent(taskMessage -> {
                        KoalaStudentTaskRecord taskRecord = new KoalaStudentTaskRecord();
                        taskRecord.setCreateTime(new Date(System.currentTimeMillis()));
                        taskRecord.setSubmitTime(new Date(taskMessage.getCreateTime()));
                        taskRecord.setTaskType(taskType.name());
                        taskRecord.setStudentId(taskMessage.getUserId());
                        taskRecord.setErScore(taskMessage.getErScore());
                        koalaStudentTaskRecordService.save(taskRecord);
                    });
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
        }
    }

    @Scheduled(fixedRate = 1000)
    public void testTask() {
        InsightMessageTypeEnum.USER_BOOK_TASK.publish(
                InsightUserTaskMessage.builder()
                        .erScore(100)
                        .userId(new Random().nextLong())
                        .createTime(System.currentTimeMillis())
                        .build()
        );

        InsightMessageTypeEnum.USER_ER_TASK.publish(
                InsightUserTaskMessage.builder()
                        .erScore(100)
                        .userId(new Random().nextLong())
                        .createTime(System.currentTimeMillis())
                        .build()
        );

        InsightMessageTypeEnum.USER_STEP_TASK.publish(
                InsightUserTaskMessage.builder()
                        .erScore(100)
                        .userId(new Random().nextLong())
                        .createTime(System.currentTimeMillis())
                        .build()
        );
    }
}
