package com.xykj.koala.vo;

import com.xykj.koala.core.InsightExceptions;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.List;

import static org.springframework.util.Assert.notNull;

/**
 * @author liuzhihao
 * @date 2018/4/16
 */
@Data
public class StaffAdminRoleVO {

    private Long staffId;

    private Long roleId;

    private String mobile;

    private String employeeName;

    private String email;

    private List<Region> adminRegions;

    public static void checkParams(StaffAdminRoleVO staffAdminRoleVO) {
        try {
            String message = "";

            notNull(staffAdminRoleVO, message);
            notNull(staffAdminRoleVO.getRoleId(), message);
            notNull(staffAdminRoleVO.getEmail(), message);
            List<Region> adminRegions = staffAdminRoleVO.getAdminRegions();
            notNull(adminRegions, message);
            Assert.notEmpty(adminRegions, message);
        } catch (IllegalArgumentException exception) {
            throw InsightExceptions.illegalArgumentExceptionSupplier.get();
        }
    }
}
