package com.xykj.koala.insight.message;

import com.xykj.koala.insight.util.InsightMessageProducer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuzhihao
 * @date 2018/4/11
 */
@Slf4j
public enum InsightMessageTypeEnum {

    /**
     * 新学校创建
     */
    SCHOOL_CREATED {
    },
    /**
     * 新用户创建事件
     */
    USER_CREATED {
        @Override
        public <T extends AbstractInsightMessage> void publish(T message) {
            InsightMessageProducer.sendUserCreateMessage(message.toJson());
        }

    },
    /**
     * 用户基本信息修改
     */
    //TODO
    USER_MODIFIED {
        @Override
        public <T extends AbstractInsightMessage> void publish(T message) {
            InsightMessageProducer.sendUserModifyMessage(message.toJson());
        }
    },
    /**
     * 用户加入班级
     */
    USER_JOIN_CLASS {
        @Override
        public <T extends AbstractInsightMessage> void publish(T message) {
            InsightMessageProducer.sendUserJoinClassMessage(message.toJson());
        }
    },
    /**
     * 用户离开班级
     */
    USER_LEAVE_CLASS {
        @Override
        public <T extends AbstractInsightMessage> void publish(T message) {
            InsightMessageProducer.sendUserLeaveClassMessage(message.toJson());
        }
    },
    /**
     * 用户初始能力测评
     */
    USER_ER_TASK {
        @Override
        public <T extends AbstractInsightMessage> void publish(T message) {
            InsightMessageProducer.sendUserERTaskMessage(message.toJson());
        }
    },
    /**
     * 用户完成书籍任务
     */
    USER_BOOK_TASK {
        @Override
        public <T extends AbstractInsightMessage> void publish(T message) {
            InsightMessageProducer.sendUserBookTaskMessage(message.toJson());
        }
    },
    /**
     * 用户完成短文任务
     */
    USER_STEP_TASK {
        @Override
        public <T extends AbstractInsightMessage> void publish(T message) {
            InsightMessageProducer.sendUserStepTaskMessage(message.toJson());
        }
    };

    public <T extends AbstractInsightMessage> void publish(T message) {
        throw new AbstractMethodError();
    }

}
