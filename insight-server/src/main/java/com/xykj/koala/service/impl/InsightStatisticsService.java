package com.xykj.koala.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.xykj.koala.core.InsightExceptions;
import com.xykj.koala.core.InsightSystemContext;
import com.xykj.koala.dao.InsightStatisticsMapper;
import com.xykj.koala.dao.InternalDAO;
import com.xykj.koala.model.InsightStaffRegion;
import com.xykj.koala.model.KoalaClass;
import com.xykj.koala.service.*;
import com.xykj.koala.utils.DateUtils;
import com.xykj.koala.utils.RegionUtils;
import com.xykj.koala.vo.*;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.xykj.koala.utils.FormatUtils.collectionToString;
import static com.xykj.koala.utils.FormatUtils.computeRatio;
import static com.xykj.koala.vo.StatisticsChartVO.CHART_SUM_OPERATOR;
import static com.xykj.koala.vo.StatisticsResultVO.SUM_REDUCER;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author liuzhihao
 * @date 2018/4/17
 */
@Component
public class InsightStatisticsService {

    @Resource
    private ExecutorService executorService;

    @Resource
    private InsightStatisticsMapper insightStatisticsMapper;

    @Resource
    private InsightStaffService insightStaffService;

    @Resource
    private InternalDAO internalDAO;

    /**
     * 根据用户角色生成他的首页数据
     *
     * @param staffId 当前用户id
     * @param date    查询结束时间
     * @return 首页，数据结构包括总览、角色列表，数据列表，直方图，操作日志
     */
    public StatisticsPageVO queryForHome(long staffId, String date) {
        InsightUserRoleVO target = internalDAO.findStaffBy(staffId)
                .orElseThrow(InsightExceptions.permissionDeniedExceptionSupplier);

        switch (target.getRoleLevel()) {
            case 0:
                return this.queryHomeForSuperAdmin(staffId, date);
            case 1:
                return this.queryHomeForLv1(staffId, date);
            case 2:
                return this.queryHomeForLv2(staffId, date);
            case 3:
                return this.queryHomeForLv3(staffId, date);
            default:
                throw InsightExceptions.permissionDeniedExceptionSupplier.get();
        }

    }


    public StatisticsPageVO queryByCity(long staffId, long cityId, String date) {
        List<StatisticsResultVO> result = this.getCachedDataToday(date)
                .parallelStream()
                .filter(r -> Objects.equals(cityId, r.getCityId()))
                .collect(Collectors.toList());

        Map<Long, List<StatisticsResultVO>> groupByStaffs = result.stream()
                .collect(Collectors.groupingBy(StatisticsResultVO::getStaffId));

        List<StatisticsResultVO> staffResult = groupByStaffs.keySet()
                .parallelStream()
                .map(s -> {
                    List<StatisticsResultVO> citiesResult = groupByStaffs.get(s);
                    StatisticsResultVO sum = citiesResult
                            .parallelStream()
                            .reduce(SUM_REDUCER)
                            .orElseGet(StatisticsResultVO::createDefault);

                    sum.setStaffId(s);
                    sum.setTotalRatio(computeRatio(sum.getTotalQualified(), sum.getTotalActual()));
                    this.findChildren(staffId, this.internalDAO.findAllStaffs())
                            .stream()
                            .filter(r -> Objects.equals(r.getStaffId(), s))
                            .findFirst()
                            .ifPresent(role -> sum.setEmployeeName(role.getEmployeeName()));
                    return sum;
                })
                .collect(Collectors.toList());

        StatisticsResultVO total = staffResult.parallelStream().reduce(SUM_REDUCER).orElseGet(StatisticsResultVO::createDefault);

        return StatisticsPageVO.builder()
                .list(staffResult)
                .overview(StatisticsOverview.builder()
                        .totalQualified(total.getTotalQualified())
                        .totalJoined(total.getTotalJoined())
                        .totalRatio(computeRatio(total.getTotalQualified(), total.getTotalJoined()))
                        .build())
                .build();
    }

    public StatisticsPageVO queryByProvince(long staffId, long provinceId, String date) {
        List<StatisticsResultVO> result = this.getCachedDataToday(date)
                .parallelStream()
                .filter(r -> Objects.equals(r.getProvinceId(), provinceId))
                .collect(Collectors.toList());

        Map<Long, List<StatisticsResultVO>> groupByCities = result.stream()
                .collect(Collectors.groupingBy(StatisticsResultVO::getCityId));

        List<StatisticsResultVO> citiesResults =
                groupByCities.keySet()
                        .parallelStream()
                        .map(cityId -> {
                            List<StatisticsResultVO> cities = groupByCities.get(cityId);
                            StatisticsResultVO citySum = cities.parallelStream().reduce(SUM_REDUCER).orElseGet(StatisticsResultVO::createDefault);
                            citySum.setCityId(cityId);
                            citySum.setCityName(RegionUtils.getRegionName(cityId));
                            return citySum;
                        }).collect(Collectors.toList());

        StatisticsResultVO total = citiesResults.parallelStream().reduce(SUM_REDUCER).orElseGet(StatisticsResultVO::createDefault);

        return StatisticsPageVO.builder()
                .overview(StatisticsOverview.builder()
                        .totalRatio(computeRatio(total.getTotalQualified(), total.getTotalJoined()))
                        .totalJoined(total.getTotalJoined())
                        .totalQualified(total.getTotalQualified())
                        .build())
                .list(citiesResults)
                .build();
    }

    @Resource
    private KoalaSchoolService koalaSchoolService;

    private StatisticsPageVO queryHomeForLv3(long staffId, String date) {
        LocalDate statDate = DateUtils.computeTheLastDayOfLastMonth(date);
        FutureTask<Integer> totalQuantityBeforeThisMonth = new FutureTask<>(() -> insightStatisticsMapper.selectTotalActualBefore(statDate));
        executorService.submit(totalQuantityBeforeThisMonth);

        FutureTask<List<StatisticsResultVO>> schools = new FutureTask<>(() -> {

            List<StatisticsResultVO> result = insightStatisticsMapper.statisticsToday(date);

            Map<Long, List<StatisticsResultVO>> groupBySchools = result.parallelStream()
                    .filter(r -> Objects.equals(staffId, r.getStaffId()))
                    .collect(Collectors.groupingBy(StatisticsResultVO::getSchoolId));

            return groupBySchools.keySet()
                    .stream()
                    .map(schoolId -> {
                        StatisticsResultVO sum = groupBySchools.get(schoolId)
                                .stream()
                                .reduce(SUM_REDUCER)
                                .orElse(StatisticsResultVO.createDefault());

                        sum.setSchoolId(schoolId);
                        sum.setSchoolName(koalaSchoolService.findBySchoolId(schoolId).getSchoolName());
                        sum.setTotalRatio(computeRatio(sum.getTotalQualified(), sum.getTotalActual()));
                        return sum;
                    })
                    .collect(Collectors.toList());
        });
        executorService.submit(schools);

        FutureTask<StatisticsChartVO> chart = new FutureTask<>(() -> {
            //返回我管理的所有班级的直方图数据
            List<StatisticsChartVO> cts = insightStatisticsMapper.selectChartsOf(staffId, date);
            if (isEmpty(cts)) {
                return null;
            }
            return cts.parallelStream()
                    .reduce(StatisticsChartVO.CHART_SUM_OPERATOR)
                    .orElseGet(StatisticsChartVO::createDefault);
        });
        executorService.submit(chart);

        FutureTask<Object> logs = new FutureTask<>(Lists::newArrayList);
        executorService.submit(logs);
        try {
            List<StatisticsResultVO> statisticsResults = schools.get();
            Integer totalQualified = statisticsResults.stream().reduce(SUM_REDUCER).orElseGet(StatisticsResultVO::createDefault).getTotalQualified();
            Integer totalJoinedBeforeThisMonthValue = totalQuantityBeforeThisMonth.get();
            return StatisticsPageVO.builder()
                    .overview(StatisticsOverview.builder()
                            .totalQualified(totalQualified)
                            .totalLastMonth(totalJoinedBeforeThisMonthValue)
                            .totalRatio(computeRatio(totalQualified, totalJoinedBeforeThisMonthValue))
                            .build())
                    .list(statisticsResults)
                    .chart(chart.get())
                    .build();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Resource
    private InsightStaffRegionService insightStaffRegionService;

    private StatisticsPageVO queryHomeForLv2(long staffId, String date) {
        LocalDate statDate = DateUtils.computeTheLastDayOfLastMonth(date);
        FutureTask<Integer> totalQuantityBeforeThisMonth = new FutureTask<>(() -> insightStatisticsMapper.selectTotalActualBefore(statDate));
        executorService.submit(totalQuantityBeforeThisMonth);

        FutureTask<List<StatisticsResultVO>> provinces = new FutureTask<>(() -> {
            //我名下所有的三级人员
            List<Long> adminsLv3 = this.findChildrenStaffs(staffId);
            String ids = collectionToString(adminsLv3);

            //三级人员的数据并找到每个人所在的省,按provinceId分组为一个map
            List<StatisticsResultVO> result = insightStatisticsMapper.statisticsFor(ids, date);
            Map<Long, List<StatisticsResultVO>> groupByProvince = result.stream()
                    .peek(r -> {
                        Condition condition = new Condition(InsightStaffRegion.class);
                        condition.and().andEqualTo("staffId", r.getStaffId());
                        insightStaffRegionService.findByCondition(condition)
                                .stream()
                                .findFirst()
                                .ifPresent(region -> {
                                    r.setProvinceId(region.getProvinceId());
                                    r.setProvinceName(RegionUtils.getRegionName(region.getProvinceId()));
                                });
                    }).collect(Collectors.groupingBy(StatisticsResultVO::getProvinceId));

            //把map转换为最终的结果列表
            return groupByProvince.keySet().stream()
                    .map(provinceId -> {
                        List<StatisticsResultVO> admin3Results = groupByProvince.get(provinceId);
                        return admin3Results.stream().reduce(SUM_REDUCER).orElseGet(StatisticsResultVO::createDefault);
                    }).collect(Collectors.toList());
        });
        executorService.submit(provinces);

        try {
            List<StatisticsResultVO> statisticsResults = provinces.get();
            Integer totalQualified = statisticsResults.stream().reduce(SUM_REDUCER).orElseGet(StatisticsResultVO::createDefault).getTotalQualified();
            Integer totalJoinedBeforeThisMonthValue = totalQuantityBeforeThisMonth.get();

            return StatisticsPageVO.builder()
                    .overview(StatisticsOverview.builder()
                            .totalQualified(totalQualified)
                            .totalLastMonth(totalJoinedBeforeThisMonthValue)
                            .totalRatio(computeRatio(totalQualified, totalJoinedBeforeThisMonthValue))
                            .build())
                    .list(statisticsResults)
                    .build();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private StatisticsPageVO queryHomeForLv1(long staffId, String date) {
        Long roleId = insightStaffService.findManageableRolesOf(staffId)
                .stream()
                .min(Comparator.comparing(InsightUserRoleVO::getRoleLevel))
                .orElseThrow(InsightExceptions.permissionDeniedExceptionSupplier)
                .getRoleId();
        return this.queryByRole(staffId, roleId, date);
    }

    private List<Long> findChildrenStaffs(long staffId) {
        return this.internalDAO.findAllStaffs().stream()
                .filter(s -> Objects.equals(staffId, s.getAdminId()))
                .map(InsightUserRoleVO::getStaffId)
                .distinct()
                .collect(Collectors.toList());
    }

    private StatisticsPageVO queryHomeForSuperAdmin(long staffId, String date) {
        InsightUserRoleVO me = InsightSystemContext.getInsightStaff();
        if (!me.isSuperAdmin()) {
            throw InsightExceptions.permissionDeniedExceptionSupplier.get();
        }
        return this.queryHomeForLv1(staffId, date);
    }

    private List<InsightUserRoleVO> findChildren(long parentId, List<InsightUserRoleVO> children) {
        List<InsightUserRoleVO> res = Lists.newArrayList();
        List<InsightUserRoleVO> result = children.stream().filter(r -> Objects.equals(parentId, r.getAdminId())).collect(Collectors.toList());
        res.addAll(result);
        result.forEach(r -> res.addAll(findChildren(r.getStaffId(), this.internalDAO.findAllStaffs())));
        return res;
    }

    public StatisticsPageVO queryByRole(long staffId, long roleId, String date) {
        LocalDate statDate = DateUtils.computeTheLastDayOfLastMonth(date);
        FutureTask<Integer> totalQuantityBeforeThisMonth = new FutureTask<>(() -> insightStatisticsMapper.selectTotalActualBefore(statDate));
        executorService.submit(totalQuantityBeforeThisMonth);

        FutureTask<List<StatisticsResultVO>> staffs = new FutureTask<>(() -> {
            List<StatisticsResultVO> cachedDataToday = this.getCachedDataToday(date);
            Map<Long, List<StatisticsResultVO>> admin3Results = cachedDataToday.parallelStream()
                    .collect(Collectors.groupingBy(StatisticsResultVO::getStaffId));

            List<InsightUserRoleVO> allStaffs = this.internalDAO.findAllStaffs();
            return allStaffs.parallelStream()
                    .filter(s -> Objects.equals(s.getRoleId(), roleId))
                    .map(c -> {
                        List<InsightUserRoleVO> children = this.findChildren(c.getStaffId(), this.internalDAO.findAllStaffs());
                        StatisticsResultVO sumOfAdmin1;
                        if (isEmpty(children)) {
                            sumOfAdmin1 = StatisticsResultVO.createDefault();
                            sumOfAdmin1.setStaffId(c.getStaffId());
                            internalDAO.findStaffBy(c.getStaffId()).ifPresent(s -> sumOfAdmin1.setEmployeeName(s.getEmployeeName()));
                            return sumOfAdmin1;
                        }

                        return children.stream()
                                .filter(r -> Objects.equals(r.getRoleValue(), InsightUserRoleVO.ADMIN_LEVEL_3))
                                .map(r -> {
                                    //每个三级的统计数据进行汇总
                                    List<StatisticsResultVO> statisticsResultVOS = admin3Results.get(r.getStaffId());
                                    if (isEmpty(statisticsResultVOS)) {
                                        StatisticsResultVO sumOfAdmin2 = StatisticsResultVO.createDefault();
                                        sumOfAdmin2.setStaffId(c.getStaffId());
                                        internalDAO.findStaffBy(c.getStaffId()).ifPresent(s -> sumOfAdmin2.setEmployeeName(s.getEmployeeName()));
                                        return sumOfAdmin2;
                                    }

                                    StatisticsResultVO sumOfAdmin3 = statisticsResultVOS.parallelStream()
                                            .reduce(StatisticsResultVO.SUM_REDUCER)
                                            .orElseThrow(RuntimeException::new);
                                    sumOfAdmin3.setStaffId(c.getStaffId());
                                    sumOfAdmin3.setTotalRatio(computeRatio(sumOfAdmin3.getTotalQualified(), sumOfAdmin3.getTotalActual()));
                                    internalDAO.findStaffBy(c.getStaffId()).ifPresent(s -> sumOfAdmin3.setEmployeeName(s.getEmployeeName()));

                                    return sumOfAdmin3;
                                })
                                .findFirst()
                                .orElseGet(() -> {
                                    StatisticsResultVO def = StatisticsResultVO.createDefault();
                                    def.setStaffId(c.getStaffId());
                                    internalDAO.findStaffBy(c.getStaffId()).ifPresent(s -> def.setEmployeeName(s.getEmployeeName()));

                                    return def;
                                });
                    })
                    .collect(Collectors.toList());
        });
        executorService.submit(staffs);

        FutureTask<List<InsightUserRoleVO>> roles = new FutureTask<>(() -> insightStaffService.findManageableRolesOf(staffId));
        executorService.submit(roles);

        try {
            List<StatisticsResultVO> statisticsResults = staffs.get();
            Integer totalQualified = statisticsResults.stream().reduce(SUM_REDUCER).orElseGet(StatisticsResultVO::createDefault).getTotalQualified();
            Integer totalLastMonth = totalQuantityBeforeThisMonth.get();

            return StatisticsPageVO.builder()
                    .overview(StatisticsOverview.builder()
                            .totalLastMonth(totalLastMonth)
                            .totalRatio(computeRatio(totalQualified, totalLastMonth))
                            .totalQualified(totalQualified)
                            .build())
                    .list(statisticsResults)
                    .roles(roles.get())
                    .build();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public StatisticsPageVO queryByDistrict(Long targetStaffId, Long districtId, String date) {

        FutureTask<StatisticsChartVO> chart = new FutureTask<>(() -> {
            Condition condition = new Condition(InsightStaffRegion.class);
            condition.and().andEqualTo("staffId", targetStaffId).andEqualTo("districtId", districtId);
            List<InsightStaffRegion> regions = this.insightStaffRegionService.findByCondition(condition);
            if (isEmpty(regions)) {
                return StatisticsChartVO.createDefault();
            }

            //返回我的所有班级的直方图数据
            List<StatisticsChartVO> charts = insightStatisticsMapper.selectChartsOf(targetStaffId, date);
            //只保留当前区县的班级
            Map<Long, List<StatisticsChartVO>> groupByDistrict = charts.parallelStream()
                    .peek(c -> {
                        //设置区县id
                        c.setDistrictId(districtId);
                    })
                    .collect(Collectors.groupingBy(StatisticsChartVO::getDistrictId));

            return groupByDistrict.keySet()
                    .parallelStream()
                    .filter(key -> Objects.equals(key, districtId))
                    .map(id -> {
                        List<StatisticsChartVO> districtCharts = groupByDistrict.get(id);
                        return districtCharts.stream().reduce(CHART_SUM_OPERATOR).orElseGet(StatisticsChartVO::createDefault);
                    }).findFirst().orElseGet(StatisticsChartVO::createDefault);
        });
        executorService.submit(chart);

        FutureTask<List<StatisticsResultVO>> list = new FutureTask<>(() ->
                this.getCachedDataToday(date)
                        .parallelStream()
                        .filter(r -> Objects.equals(r.getDistrictId(), districtId))
                        .collect(Collectors.groupingBy(StatisticsResultVO::getSchoolId))
                        .entrySet()
                        .parallelStream()
                        .map(school -> {
                            StatisticsResultVO sumVO = school.getValue().parallelStream().reduce(SUM_REDUCER).orElse(StatisticsResultVO.createDefault());
                            sumVO.setSchoolId(school.getKey());
                            sumVO.setTotalRatio(computeRatio(sumVO.getTotalQualified(), sumVO.getTotalActual()));

                            return sumVO;
                        })
                        .collect(Collectors.toList()));
        executorService.submit(list);

        try {
            List<StatisticsResultVO> schoolResults = list.get();
            StatisticsResultVO total = schoolResults.parallelStream().reduce(SUM_REDUCER).orElseGet(StatisticsResultVO::createDefault);

            StatisticsPageVO.builder()
                    .overview(StatisticsOverview.builder()
                            .totalQualified(total.getTotalQualified())
                            .totalJoined(total.getTotalActual())
                            .totalRatio(computeRatio(total.getTotalQualified(), total.getTotalActual()))
                            .build())
                    .list(schoolResults)
                    .chart(chart.get())
                    .logs(Collections.emptyList())
                    .build();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<StatisticsResultVO> getCachedDataToday(String date) {
        try {
            return TODAY_STATISTICS_RESULT.get("ALL_CLASS_STATISTICS_RESULT" + date.replace("-", ""),
                    () -> insightStatisticsMapper.statisticsToday(date));
        } catch (ExecutionException e) {
            return insightStatisticsMapper.statisticsToday(date);
        }
    }

    @Resource
    private KoalaClassService koalaClassService;

    public static final Cache<String, List<StatisticsResultVO>> TODAY_STATISTICS_RESULT =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(12, TimeUnit.HOURS)
                    .build();

    public StatisticsPageVO queryBySchool(Long targetStaffId, Long schoolId, String date) {

        FutureTask<StatisticsChartVO> chart = new FutureTask<>(() -> {
            //返回这所学校的直方图汇总
            List<StatisticsChartVO> charts = insightStatisticsMapper.selectChartsOf(targetStaffId, date);
            if (isEmpty(charts)) {
                return null;
            }
            //只保留当前学校的班级
            Map<Long, List<StatisticsChartVO>> groupBySchools = charts.parallelStream()
                    .collect(Collectors.groupingBy(StatisticsChartVO::getSchoolId));

            return groupBySchools.keySet()
                    .parallelStream()
                    .filter(school -> Objects.equals(school, schoolId))
                    .map(id -> {
                        List<StatisticsChartVO> schoolCharts = groupBySchools.get(id);
                        return schoolCharts.stream().reduce(CHART_SUM_OPERATOR).orElseGet(StatisticsChartVO::createDefault);
                    }).findFirst().orElseGet(StatisticsChartVO::createDefault);
        });
        executorService.submit(chart);

        FutureTask<List<StatisticsResultVO>> list = new FutureTask<>(() ->
                this.getCachedDataToday(date)
                        .parallelStream()
                        .filter(r -> Objects.equals(r.getSchoolId(), schoolId))
                        .collect(Collectors.groupingBy(StatisticsResultVO::getClassId))
                        .entrySet()
                        .parallelStream()
                        .map(classId -> {
                            StatisticsResultVO sumVO = classId.getValue().parallelStream().reduce(SUM_REDUCER).orElse(StatisticsResultVO.createDefault());
                            sumVO.setClassId(classId.getKey());
                            sumVO.setTotalRatio(computeRatio(sumVO.getTotalQualified(), sumVO.getTotalActual()));

                            KoalaClass koalaClass = koalaClassService.findByClassId(classId.getKey());
                            sumVO.setClassName(koalaClass.getClassName());
                            sumVO.setTeacherName(koalaClass.getTeacherName());
                            return sumVO;
                        })
                        .collect(Collectors.toList()));
        executorService.submit(list);

        try {
            List<StatisticsResultVO> schoolResults = list.get();
            StatisticsResultVO total = schoolResults.parallelStream().reduce(SUM_REDUCER).orElseGet(StatisticsResultVO::createDefault);

            return StatisticsPageVO.builder()
                    .overview(StatisticsOverview.builder()
                            .totalQualified(total.getTotalQualified())
                            .totalJoined(total.getTotalActual())
                            .totalRatio(computeRatio(total.getTotalQualified(), total.getTotalActual()))
                            .build())
                    .list(schoolResults)
                    .chart(chart.get())
                    .build();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public StatisticsPageVO queryByClass(Long targetStaffId, Long classId, String date) {
        FutureTask<StatisticsChartVO> chart = new FutureTask<>(() -> {
            List<StatisticsChartVO> charts = insightStatisticsMapper.selectChartsOf(targetStaffId, date);
            Map<Long, List<StatisticsChartVO>> groupByClass = charts.parallelStream()
                    .filter(r -> Objects.equals(r.getClassId(), classId))
                    .collect(Collectors.groupingBy(StatisticsChartVO::getClassId));

            return groupByClass.keySet()
                    .parallelStream()
                    .filter(c -> Objects.equals(c, classId))
                    .map(id -> {
                        List<StatisticsChartVO> schoolCharts = groupByClass.get(id);
                        return schoolCharts.stream().reduce(CHART_SUM_OPERATOR).orElseGet(StatisticsChartVO::createDefault);
                    }).findFirst().orElseGet(StatisticsChartVO::createDefault);
        });
        executorService.submit(chart);

        FutureTask<StatisticsResultVO> detail = new FutureTask<>(() ->
                this.getCachedDataToday(date)
                        .parallelStream()
                        .filter(r -> Objects.equals(r.getClassId(), classId))
                        .findFirst()
                        .orElseGet(StatisticsResultVO::createDefault));
        executorService.submit(detail);
        try {
            StatisticsResultVO detailValue = detail.get();
            return StatisticsPageVO.builder().
                    chart(chart.get())
                    .overview(StatisticsOverview.builder()
                            .totalJoined(detailValue.getTotalJoined())
                            .totalQualified(detailValue.getTotalQualified())
                            .totalRatio(computeRatio(detailValue.getTotalQualified(), detailValue.getTotalActual()))
                            .build())
                    .build();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

}