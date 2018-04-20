package com.xykj.koala.insight.util;

import com.xykj.koala.insight.common.InsightMessageConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liuzhihao
 * @date 2018/4/11
 */
@Component
@Slf4j
public final class InsightMessageProducer {

    private static RabbitTemplate rabbitTemplate;

    private static final ReentrantLock SENDER_LOCK = new ReentrantLock(false);

    @Autowired
    private void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        InsightMessageProducer.rabbitTemplate = rabbitTemplate;
    }

    private static void sendMessage(String queueName, String routingKey, String content) {
        try {
            SENDER_LOCK.lock();

            rabbitTemplate.setQueue(queueName);
            rabbitTemplate.setRoutingKey(routingKey);
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            Message message = new Message(content.getBytes(), messageProperties);

            rabbitTemplate.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
        } finally {
            SENDER_LOCK.unlock();
        }
    }

    public static void sendSchoolClassMessage(String json) {
    }

    public static void sendUserCreateMessage(String json) {
        sendMessage(InsightMessageConstants.INSIGHT_USER_CREATE_QUEUE, InsightMessageConstants.INSIGHT_USER_CREATE_QUEUE_KEY, json);
    }

    public static void sendUserModifyMessage(String json) {
        sendMessage(InsightMessageConstants.INSIGHT_USER_MODIFY_QUEUE, InsightMessageConstants.INSIGHT_USER_MODIFY_QUEUE_KEY, json);
    }

    public static void sendUserJoinClassMessage(String json) {
        sendMessage(InsightMessageConstants.INSIGHT_JOIN_CLASS_QUEUE, InsightMessageConstants.INSIGHT_JOIN_CLASS_QUEUE_KEY, json);
    }

    public static void sendUserLeaveClassMessage(String json) {
        sendMessage(InsightMessageConstants.INSIGHT_LEAVE_CLASS_QUEUE, InsightMessageConstants.INSIGHT_LEAVE_CLASS_QUEUE_KEY, json);
    }

    public static void sendUserERTaskMessage(String json) {
        sendMessage(InsightMessageConstants.INSIGHT_ER_TEST_QUEUE, InsightMessageConstants.INSIGHT_ER_TEST_QUEUE_KEY, json);
    }

    public static void sendUserBookTaskMessage(String json) {
        sendMessage(InsightMessageConstants.INSIGHT_BOOK_TASK_QUEUE, InsightMessageConstants.INSIGHT_BOOK_TASK_QUEUE_KEY, json);
    }

    public static void sendUserStepTaskMessage(String json) {
        sendMessage(InsightMessageConstants.INSIGHT_STEP_TASK_QUEUE, InsightMessageConstants.INSIGHT_STEP_TASK_QUEUE_KEY, json);
    }

}
