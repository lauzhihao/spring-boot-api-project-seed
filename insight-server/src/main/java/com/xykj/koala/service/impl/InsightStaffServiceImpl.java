package com.xykj.koala.service.impl;

import com.google.common.collect.Lists;
import com.xykj.koala.core.InsightSystemContext;
import com.xykj.koala.dao.InternalDAO;
import com.xykj.koala.model.InsightStaffRegion;
import com.xykj.koala.service.InsightStaffRegionService;
import com.xykj.koala.service.InsightStaffService;
import com.xykj.koala.utils.RegionUtils;
import com.xykj.koala.vo.InsightUserRoleVO;
import com.xykj.koala.vo.Region;
import com.xykj.koala.vo.RegionVO;
import com.xykj.koala.vo.StaffAdminRoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.xykj.koala.core.InsightExceptions.permissionDeniedExceptionSupplier;
import static com.xykj.koala.core.InsightExceptions.targetNotFoundExceptionSupplier;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author liuzhihao
 * @date 2018/4/16
 */
@Component
@Slf4j
public class InsightStaffServiceImpl implements InsightStaffService {

    @Resource
    private InternalDAO internalDAO;

    @Override
    public List<InsightUserRoleVO> findManageableRolesOf(long staffId) {
        return internalDAO.findUserRolesOf(staffId);
    }

    @Override
    public List<RegionVO> findManageableRegionsOf(long staffId, long regionId) {
        List<RegionVO> children = RegionUtils.findChildrenOf(regionId);

        //如果我是超管，直接返回所有地区
        if (InsightSystemContext.getInsightStaff().isSuperAdmin()) {
            children.add(RegionVO.createDefault());
            return children;
        }

        //查询用户管理的区域列表
        Condition condition = new Condition(InsightStaffRegion.class);
        condition.and().andEqualTo("staffId", staffId);
        List<InsightStaffRegion> staffRegions = insightStaffRegionService.findByCondition(condition);
        if (isEmpty(staffRegions)) {
            return Collections.emptyList();
        }

        Set<Long> regionIds = staffRegions.stream()
                .map(r -> {
                    List<Long> regions = Lists.newArrayList();
                    if (Objects.equals(r.getProvinceId(), 0L)) {
                        List<Long> provinces = RegionUtils.findChildrenOf(r.getCityId()).stream().map(RegionVO::getId).collect(Collectors.toList());
                        provinces.forEach(p -> {
                            List<Long> cities = RegionUtils.findChildrenOf(p).stream().map(RegionVO::getId).collect(Collectors.toList());
                            cities.forEach(c -> {
                                List<Long> districts = RegionUtils.findChildrenOf(c).stream().map(RegionVO::getId).collect(Collectors.toList());
                                regions.addAll(districts);
                            });
                            regions.addAll(cities);
                        });
                        regions.addAll(provinces);

                        return regions;
                    }

                    if (Objects.equals(r.getCityId(), 0L)) {
                        List<Long> cities = RegionUtils.findChildrenOf(r.getProvinceId()).stream().map(RegionVO::getId).collect(Collectors.toList());
                        cities.forEach(c -> {
                            List<Long> districts = RegionUtils.findChildrenOf(c).stream().map(RegionVO::getId).collect(Collectors.toList());
                            regions.addAll(districts);
                        });
                        regions.addAll(cities);

                        return regions;
                    }

                    if (Objects.equals(r.getDistrictId(), 0L)) {
                        List<Long> districts = RegionUtils.findChildrenOf(r.getCityId()).stream().map(RegionVO::getId).collect(Collectors.toList());
                        regions.addAll(districts);
                        return regions;
                    }

                    regions.addAll(newArrayList(r.getCityId(), r.getProvinceId(), r.getDistrictId(), r.getCountryId()));
                    return regions;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        children = children.stream()
                .filter(r -> regionIds.contains(r.getId()))
                .collect(Collectors.toList());
        children.add(RegionVO.createDefault());
        return children;
    }

    @Resource
    private InsightStaffRegionService insightStaffRegionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveStaffAdminRoles(StaffAdminRoleVO staffAdminRoleVO) {
        final InsightUserRoleVO target = internalDAO.findStaffByEmail(staffAdminRoleVO.getEmail())
                .orElseThrow(targetNotFoundExceptionSupplier);

        InsightUserRoleVO me = InsightSystemContext.getInsightStaff();
        checkPermission(target, me);

        internalDAO.updateSystemRolesOf(target, staffAdminRoleVO.getRoleId());

        insightStaffRegionService.deleteRegionsOf(target.getStaffId());
        List<InsightStaffRegion> insightStaffRegions = staffAdminRoleVO.getAdminRegions()
                .stream()
                .map(r -> {
                    InsightStaffRegion insightStaffRegion = new InsightStaffRegion();
                    BeanUtils.copyProperties(r, insightStaffRegion);
                    insightStaffRegion.setStaffId(target.getStaffId());

                    return insightStaffRegion;
                })
                .collect(Collectors.toList());
        insightStaffRegionService.save(insightStaffRegions);
    }

    @Override
    public List<StaffAdminRoleVO> findManageableStaffs(long staffId, long roleId) {
        List<StaffAdminRoleVO> staffs = internalDAO.findStaffsByRoleId(staffId, roleId);
        if (isEmpty(staffs)) {
            return Collections.emptyList();
        }

        Condition condition = new Condition(InsightStaffRegion.class);
        condition.and().andIn("staffId", staffs.stream().map(StaffAdminRoleVO::getStaffId).collect(Collectors.toList()));
        List<InsightStaffRegion> staffRegions = insightStaffRegionService.findByCondition(condition);
        if (isEmpty(staffRegions)) {
            return Collections.emptyList();
        }
        Map<Long, List<InsightStaffRegion>> userRegions = staffRegions.stream().collect(groupingBy(InsightStaffRegion::getStaffId, mapping(r -> r, Collectors.toList())));

        staffs.forEach(s -> {
            List<InsightStaffRegion> insightStaffRegions = userRegions.get(s.getStaffId());
            if (isEmpty(insightStaffRegions)) {
                return;
            }

            s.setAdminRegions(
                    insightStaffRegions.stream()
                            .map(r -> new Region(r.getCountryId(), r.getProvinceId(), r.getCityId(), r.getDistrictId(), ""))
                            .collect(Collectors.toList())
            );
        });
        return staffs;
    }

    private void checkPermission(InsightUserRoleVO target, InsightUserRoleVO me) {
        //target的级别比我高(数越小越高)，或者target不是由我创建的，即便找到了target也不允许操作
        if (target.getRoleLevel() <= me.getRoleLevel() || !Objects.equals(target.getAdminId(), me.getStaffId())) {
            throw permissionDeniedExceptionSupplier.get();
        }
    }

}
