package com.xykj.koala.message;

import com.xykj.koala.insight.message.InsightUserCreateMessage;
import com.xykj.koala.insight.util.JsonUtils;
import com.xykj.koala.model.KoalaStudent;
import com.xykj.koala.service.KoalaStudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.util.Date;

import static com.xykj.koala.insight.common.InsightMessageConstants.INSIGHT_USER_CREATE_QUEUE;

/**
 * @author liuzhihao
 * @date 2018/4/14
 */
//@Component
@Slf4j
public class UserCreateMessageConsumer implements InsightMessageConsumer {

    @Resource
    private KoalaStudentService koalaStudentService;

    @RabbitListener(queues = INSIGHT_USER_CREATE_QUEUE)
    @Override
    @Async
    public void consume(Message message) {
        try {
            String msg = new String(message.getBody());
            log.debug(msg);
            JsonUtils.fromJson(msg, InsightUserCreateMessage.class)
                    .ifPresent(user -> {
                        KoalaStudent koalaStudent = new KoalaStudent();
                        koalaStudent.setStudentId(user.getUserId());
                        koalaStudent.setCreateTime(new Date(user.getCreateTime()));

                        koalaStudentService.save(koalaStudent);
                    });
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
        }
    }
}
