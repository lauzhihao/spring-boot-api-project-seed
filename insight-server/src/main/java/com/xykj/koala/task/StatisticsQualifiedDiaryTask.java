package com.xykj.koala.task;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xykj.koala.dao.InsightStatisticsMapper;
import com.xykj.koala.vo.RangeQuantityVO;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.xykj.koala.service.impl.InsightStatisticsService.TODAY_STATISTICS_RESULT;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * 统计每个班级截止到现在的各统计区间人数的总数
 * 1、统计区间目前分为：提交作业天数为0、1、2、3、> 3，这5个，后续可能会修改;
 * 2、时间区间为每个自然月的起止时间，由前端指定;
 * 3、quantity_range_1是统计时间内提交作业0天的学生人数，以此类推;
 * 4、每个班每天只保留一条数据;
 * 5、查询时使用当天 - 1 查找最新的一条数据
 * 6、往期数据查询该区间最后一天的数据
 *
 * @author liuzhihao
 * @date 2018/4/18
 */
@Component
@Slf4j
public class StatisticsQualifiedDiaryTask {

    @Resource
    private InsightStatisticsMapper insightStatisticsMapper;

    @Scheduled(cron = "0 30 0 * * ?")
    public void statisticsYesterday() {
        LocalDate begin = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now();

        insightStatisticsMapper.selectAllClasses()
                .forEach(classId -> {
                    if (begin.isEqual(end)) {
                        insightStatisticsMapper.insertDaily(end, classId, 0, 0, 0, 0, 0);
                        return;
                    }

                    //查询这段时间内这个班每位学生提交作业的天数
                    StatisticsParams params = StatisticsParams.builder().begin(begin).end(end).classId(classId).build();
                    //分组统计每个区间指标的人数
                    Integer range1 = range1Computer.apply(params);
                    Integer range2 = range2Computer.apply(params);
                    Integer range3 = range3Computer.apply(params);
                    Integer range4 = range4Computer.apply(params);
                    Integer range5 = range5Computer.apply(params);

                    //保存这个班截止到今天的最新数据
                    insightStatisticsMapper.insertDaily(end, classId, range1, range2, range3, range4, range5);
                });

        this.cleanCache();
    }

    private void cleanCache() {
        TODAY_STATISTICS_RESULT.cleanUp();
    }

    private static final Cache<Long, List<RangeQuantityVO>> RANGE_QUERY_CACHE =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(30, TimeUnit.SECONDS)
                    .build();

    private List<RangeQuantityVO> queryQuantity(StatisticsParams params) {
        try {
            return RANGE_QUERY_CACHE.get(params.getClassId(), () -> insightStatisticsMapper.selectSubmitQuantityMap(params.getBegin(), params.getEnd(), params.getClassId()));
        } catch (ExecutionException e) {
            e.printStackTrace();
            log.error(e.toString());
            return insightStatisticsMapper.selectSubmitQuantityMap(params.getBegin(), params.getEnd(), params.getClassId());
        }
    }

    /**
     * 查询本班内在指定时间段内提交过0天作业的人数
     */
    private Function<StatisticsParams, Integer> range1Computer = params -> insightStatisticsMapper.selectCountForRange1Between(params.getBegin(), params.getEnd(), params.getClassId());


    /**
     * 查询本班内在指定时间段内提交过1天的人数
     */
    private Function<StatisticsParams, Integer> range2Computer = params -> {
        List<RangeQuantityVO> result = this.queryQuantity(params);
        if (isEmpty(result)) {
            return 0;
        }
        Integer r = result.parallelStream().filter(m -> m.getDaysRange() == 1).findFirst().orElseGet(RangeQuantityVO::new).getQuantity();
        return Objects.isNull(r) ? 0 : r;
    };

    /**
     * 查询本班内在指定时间段内提交过2天作业的人数
     */
    private Function<StatisticsParams, Integer> range3Computer = params -> {
        List<RangeQuantityVO> result = this.queryQuantity(params);
        if (isEmpty(result)) {
            return 0;
        }
        Integer r = result.parallelStream().filter(m -> m.getDaysRange() == 2).findFirst().orElseGet(RangeQuantityVO::new).getQuantity();
        return Objects.isNull(r) ? 0 : r;
    };

    /**
     * 查询本班内在指定时间段内提交过3天作业的人数
     */
    private Function<StatisticsParams, Integer> range4Computer = params -> {
        List<RangeQuantityVO> result = this.queryQuantity(params);
        if (isEmpty(result)) {
            return 0;
        }
        Integer r = result.parallelStream().filter(m -> m.getDaysRange() == 3).findFirst().orElseGet(RangeQuantityVO::new).getQuantity();
        return Objects.isNull(r) ? 0 : r;

    };

    /**
     * 查询本班内在指定时间段内提交过3天以上作业的人数
     */
    private Function<StatisticsParams, Integer> range5Computer = params -> {
        List<RangeQuantityVO> result = this.queryQuantity(params);
        if (isEmpty(result)) {
            return 0;
        }
        Integer r = result.parallelStream().filter(m -> m.getDaysRange() > 3).findFirst().orElseGet(RangeQuantityVO::new).getQuantity();

        return Objects.isNull(r) ? 0 : r;
    };

}

@Getter
@Builder
class StatisticsParams {

    private Long classId;

    private LocalDate begin;

    private LocalDate end;
}