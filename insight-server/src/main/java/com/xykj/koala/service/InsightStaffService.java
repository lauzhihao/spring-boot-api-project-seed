package com.xykj.koala.service;

import com.xykj.koala.vo.InsightUserRoleVO;
import com.xykj.koala.vo.RegionVO;
import com.xykj.koala.vo.StaffAdminRoleVO;

import java.util.List;

/**
 * @author liuzhihao
 * @date 2018/4/16
 */
public interface InsightStaffService {

    List<InsightUserRoleVO> findManageableRolesOf(long staffId);

    List<RegionVO> findManageableRegionsOf(long staffId,long regionId);

    void saveStaffAdminRoles(StaffAdminRoleVO staffAdminRoleVO);

    List<StaffAdminRoleVO> findManageableStaffs(long staffId, long roleId);

}
