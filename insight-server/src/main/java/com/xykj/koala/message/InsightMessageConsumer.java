package com.xykj.koala.message;

import org.springframework.amqp.core.Message;

/**
 * @author liuzhihao
 * @date 2018/4/14
 */
public interface InsightMessageConsumer {

    void consume(Message message);

}