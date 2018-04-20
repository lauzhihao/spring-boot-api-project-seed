package com.xykj.koala.web;

import com.xykj.koala.core.Result;
import com.xykj.koala.core.ResultGenerator;
import com.xykj.koala.service.impl.InsightStatisticsService;
import com.xykj.koala.web.param.StatisticsParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author liuzhihao
 * @date 2018/4/17
 */
@RestController
@RequestMapping("/insight/staff/statistics")
@Slf4j
public class InsightStatisticsController {


    @Resource
    private InsightStatisticsService insightStatisticsService;

    /**
     * 根据当前用户的权限，返回不同的数据结构：
     * 所有角色都要查总览，首页的辖区总人数为上个统计日期区间的总人数之和
     * 1、超管：按一级汇总、下级角色列表、数据列表：按一级分组 -> 二级分组 -> 省份 -> 城市 -> 三级 -> 区县 -> 学校 -> 班级 -> 详情
     * 2、一级：按二级汇总、下级角色列表、数据列表：按二级分组 -> 省份 -> 城市 -> 三级 -> 区县 -> 学校 -> 班级 -> 详情
     * 3、二级：按省汇总、数据列表：按省分组 -> 城市分组 -> 三级分组 -> 区县分组 -> 学校分组 -> 班级分组 -> 班级详情
     * 4、三级：按学校汇总、按学校分组 -> 班级分组 -> 班级详情
     *
     * @param staffId          我
     * @param statisticsParams 查询参数：日期、角色等
     * @return 首页结构
     */
    @PostMapping("/home")
    public Result queryHome(@RequestParam long staffId, @RequestBody StatisticsParams statisticsParams) {

        return ResultGenerator.genSuccessResult(insightStatisticsService.queryForHome(staffId, statisticsParams.getEndDate()));
    }

    @PostMapping("/staff")
    public Result queryByStaff(@RequestParam long staffId, @RequestBody StatisticsParams statisticsParams) {

        return ResultGenerator.genSuccessResult(insightStatisticsService.queryForHome(statisticsParams.getTargetStaffId(), statisticsParams.getEndDate()));
    }

    @PostMapping("/role")
    public Result queryByRole(@RequestParam long staffId, @RequestBody StatisticsParams statisticsParams) {

        return ResultGenerator.genSuccessResult(insightStatisticsService.queryByRole(statisticsParams.getTargetStaffId(), statisticsParams.getRoleId(), statisticsParams.getEndDate()));
    }

    @PostMapping("/class")
    public Result queryByClass(@RequestParam long staffId, @RequestBody StatisticsParams statisticsParams) {

        return ResultGenerator.genSuccessResult(insightStatisticsService.queryByClass(statisticsParams.getTargetStaffId(), statisticsParams.getClassId(), statisticsParams.getEndDate()));
    }

    @PostMapping("/school")
    public Result queryBySchool(@RequestParam long staffId, @RequestBody StatisticsParams statisticsParams) {

        return ResultGenerator.genSuccessResult(insightStatisticsService.queryBySchool(statisticsParams.getTargetStaffId(), statisticsParams.getSchoolId(), statisticsParams.getEndDate()));
    }

    @PostMapping("/district")
    public Result queryByDistrict(@RequestParam long staffId, @RequestBody StatisticsParams statisticsParams) {

        return ResultGenerator.genSuccessResult(insightStatisticsService.queryByDistrict(statisticsParams.getTargetStaffId(), statisticsParams.getDistrictId(), statisticsParams.getEndDate()));
    }

    @PostMapping("/city")
    public Result queryByCity(@RequestParam long staffId, @RequestBody StatisticsParams statisticsParams) {

        return ResultGenerator.genSuccessResult(insightStatisticsService.queryByCity(statisticsParams.getTargetStaffId(), statisticsParams.getCityId(), statisticsParams.getEndDate()));
    }

    @PostMapping("/province")
    public Result queryByProvince(@RequestParam long staffId, @RequestBody StatisticsParams statisticsParams) {

        return ResultGenerator.genSuccessResult(insightStatisticsService.queryByProvince(statisticsParams.getTargetStaffId(), statisticsParams.getProvinceId(), statisticsParams.getEndDate()));
    }

}
