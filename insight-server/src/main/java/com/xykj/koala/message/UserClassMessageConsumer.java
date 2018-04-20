package com.xykj.koala.message;

import com.xykj.koala.insight.message.InsightUserClassMessage;
import com.xykj.koala.insight.util.JsonUtils;
import com.xykj.koala.model.KoalaStudentJoinedClass;
import com.xykj.koala.service.KoalaStudentJoinedClassService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

import static com.xykj.koala.insight.common.InsightMessageConstants.INSIGHT_JOIN_CLASS_QUEUE;
import static com.xykj.koala.insight.common.InsightMessageConstants.INSIGHT_LEAVE_CLASS_QUEUE;

/**
 * @author liuzhihao
 * @date 2018/4/14
 */
//@Component
public class UserClassMessageConsumer {

    @Resource
    private KoalaStudentJoinedClassService koalaStudentJoinedClassService;

    @RabbitListener(queues = {INSIGHT_LEAVE_CLASS_QUEUE})
    public void consumeLeaveMessage(@Payload Message message) {
        Optional<InsightUserClassMessage> insightUserClassMessage = JsonUtils.fromJson(
                new String(message.getBody()), InsightUserClassMessage.class);
        insightUserClassMessage.ifPresent(m -> {
            Condition condition = new Condition(KoalaStudentJoinedClass.class);
            condition.and().andEqualTo("userId", m.getUserId()).andEqualTo("classId", m.getClassId());
            koalaStudentJoinedClassService.remove(m.getUserId(), m.getClassId());
        });
    }

    @RabbitListener(queues = {INSIGHT_JOIN_CLASS_QUEUE})
    public void consumeJoinMessage(@Payload Message message) {
        Optional<InsightUserClassMessage> insightUserClassMessage = JsonUtils.fromJson(
                new String(message.getBody()), InsightUserClassMessage.class);
        insightUserClassMessage.ifPresent(m -> {
            KoalaStudentJoinedClass koalaStudentJoinedClass =
                    KoalaStudentJoinedClass
                            .builder()
                            .classId(m.getClassId())
                            .studentId(m.getUserId())
                            .createTime(new Date(m.getCreateTime()))
                            .build();
            koalaStudentJoinedClassService.save(koalaStudentJoinedClass);
        });

    }
}
