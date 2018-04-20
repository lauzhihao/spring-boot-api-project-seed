package com.xykj.koala.insight.event;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liuzhihao
 * @date 2018/4/9
 */
@Component
public class EventBusStation {

    private static final EventBus GLOBAL_EVENT_BUS = new AsyncEventBus(
            "GLOBAL_ASYNC_EVENT_BUS",
            new ThreadPoolExecutor(10, Integer.MAX_VALUE,
                    0, TimeUnit.NANOSECONDS
                    , new ArrayBlockingQueue<>(1024)
                    , new ThreadFactoryBuilder().setNameFormat("insight-event-%d").build())
    );

    @Resource
    private ApplicationContext applicationContext;

    @PostConstruct
    public void registerAllListeners() {
    }

    static void asyncPost(AbstractInsightEvent event) {
        GLOBAL_EVENT_BUS.post(event);
    }
}
