package com.xykj.koala.web;

import com.xykj.koala.core.Result;
import com.xykj.koala.core.ResultGenerator;
import com.xykj.koala.dao.KoalaClassMapper;
import com.xykj.koala.dao.KoalaSchoolMapper;
import com.xykj.koala.service.InsightStaffClassService;
import com.xykj.koala.service.InsightStaffService;
import com.xykj.koala.vo.StaffAdminRoleVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.xykj.koala.vo.StaffAdminRoleVO.checkParams;

/**
 * @author liuzhihao
 * @date 2018/4/16
 */
@Api("明察系统运营人员管理相关接口")
@RestController
@RequestMapping("/insight/staff/admin")
@Slf4j
public class InsightStaffAdminController {

    @Resource
    private InsightStaffService insightStaffService;

    @GetMapping("/roles")
    public Result getAdminRoles(@RequestParam long staffId) {
        return ResultGenerator.genSuccessResult(insightStaffService.findManageableRolesOf(staffId));
    }

    @GetMapping("/regions")
    public Result getChildrenRegionsOf(@RequestParam long staffId, @RequestParam(defaultValue = "1L") long regionId) {

        return ResultGenerator.genSuccessResult(insightStaffService.findManageableRegionsOf(staffId, regionId));
    }

    @PostMapping("/roles")
    public Result saveOrUpdateRegionRoles(@RequestBody StaffAdminRoleVO staffAdminRoleVO) {
        checkParams(staffAdminRoleVO);
        insightStaffService.saveStaffAdminRoles(staffAdminRoleVO);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping
    public Result list(@RequestParam long staffId, @RequestParam long roleId) {

        return ResultGenerator.genSuccessResult(insightStaffService.findManageableStaffs(staffId, roleId));
    }

    @Resource
    private KoalaSchoolMapper koalaSchoolMapper;

    /**
     * 查询某个区县下的所有学校，用于填充学校录入界面的下拉框
     *
     * @param staffId    我
     * @param districtId 区县id
     * @return 学校列表
     */
    @GetMapping("/schools/{districtId}")
    public Result querySchools(@RequestParam long staffId, @PathVariable long districtId) {
        return ResultGenerator.genSuccessResult(koalaSchoolMapper.selectByDistrictId(districtId));
    }

    @Resource
    private KoalaClassMapper koalaClassMapper;

    /**
     * 查询某个学校下的所有班级,用于填充班级录入界面的下拉框
     *
     * @param staffId  我
     * @param schoolId 学校id
     * @return 班级列表，包括班级名称，班级id
     */
    @GetMapping("/classes/{schoolId}")
    public Result queryClasses(@RequestParam long staffId, @PathVariable long schoolId) {

        return ResultGenerator.genSuccessResult(koalaClassMapper.selectForBinding(schoolId));
    }

    @Resource
    private InsightStaffClassService insightStaffClassService;

    /**
     * 创建我与学校的绑定关系,创建时把该学校下的所有班级同时绑定
     *
     * @param staffId 我
     */
    @PostMapping("/school/binding/{schoolId}")
    public Result createSchoolBinding(@RequestParam long staffId, @PathVariable long schoolId) {
        insightStaffClassService.createSchoolBinding(staffId, schoolId);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 删除我与学校的绑定关系,删除时把该学校所有的班级同时删除
     *
     * @param staffId 我
     */
    @DeleteMapping("/school/binding/{schoolId}")
    public Result removeSchoolBinding(@RequestParam long staffId, @PathVariable long schoolId) {
        insightStaffClassService.removeSchoolBinding(staffId, schoolId);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 更新班级的实际人数
     */
    @PutMapping("/class/binding/{classId}/{quantity}")
    public Result updateClassBinding(@RequestParam long staffId, @PathVariable long classId, @PathVariable int quantity) {
        insightStaffClassService.updateClassBinding(staffId, classId, quantity);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 删除我与班级的绑定关系
     */
    @DeleteMapping("/class/binding/{classId}")
    public Result removeClassBinding(@RequestParam long staffId, @PathVariable long classId) {
        insightStaffClassService.deleteClassBinding(staffId, classId);
        return ResultGenerator.genSuccessResult();
    }
}
