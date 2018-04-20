package com.xykj.koala.insight.configurer;

import com.google.common.collect.ImmutableMap;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static com.xykj.koala.insight.common.InsightMessageConstants.*;

/**
 * @author liuzhihao
 * @date 2018/4/11
 */
@Configuration("rabbitMQConfig")
public class RabbitMqConfigurer {

    private static final ImmutableMap<String, String> NEED_TO_DECLARE_QUEUES =
            ImmutableMap.<String, String>builder()
                    .put(INSIGHT_USER_CREATE_QUEUE, INSIGHT_USER_CREATE_QUEUE_KEY)
                    .put(INSIGHT_USER_MODIFY_QUEUE, INSIGHT_USER_MODIFY_QUEUE_KEY)
                    .put(INSIGHT_BOOK_TASK_QUEUE, INSIGHT_BOOK_TASK_QUEUE_KEY)
                    .put(INSIGHT_ER_TEST_QUEUE, INSIGHT_ER_TEST_QUEUE_KEY)
                    .put(INSIGHT_JOIN_CLASS_QUEUE, INSIGHT_JOIN_CLASS_QUEUE_KEY)
                    .put(INSIGHT_LEAVE_CLASS_QUEUE, INSIGHT_LEAVE_CLASS_QUEUE_KEY)
                    .put(INSIGHT_STEP_TASK_QUEUE, INSIGHT_STEP_TASK_QUEUE_KEY)
                    .build();

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        TopicExchange topicExchange = new TopicExchange(INSIGHT_EXCHANGE, true, false);
        topicExchange.setInternal(false);
        rabbitAdmin.declareExchange(topicExchange);

        NEED_TO_DECLARE_QUEUES.forEach((queueName, routingKey) -> {
            Queue queue = new Queue(queueName, true);
            rabbitAdmin.declareQueue(queue);
            rabbitAdmin.declareBinding(
                    BindingBuilder.bind(queue).to(topicExchange).with(routingKey)
            );
        });

        return rabbitAdmin;
    }

    @Bean
    @Primary
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {

        return this.createDefaultRabbitTemplate(connectionFactory);
    }


    private RabbitTemplate createDefaultRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setExchange(INSIGHT_EXCHANGE);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

}