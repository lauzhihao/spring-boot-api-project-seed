package com.xykj.koala.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xykj.koala.core.InsightExceptions;
import com.xykj.koala.core.ServiceException;
import com.xykj.koala.vo.InsightUserRoleVO;
import com.xykj.koala.vo.StaffAdminRoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author liuzhihao
 * @date 2018/4/16
 */
@Component
@Slf4j
public class InternalDAO {

    @Resource
    @Qualifier("koalaInternalJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private static final Integer MARKET_DEPARTMENT_ID = 2;

    private static Cache<Long, Optional<InsightUserRoleVO>> STAFF_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(8, TimeUnit.HOURS)
            .build();

    public Optional<InsightUserRoleVO> findStaffBy(long staffId) {
        try {
            return STAFF_CACHE.get(staffId, () -> findStaffById(staffId));
        } catch (ExecutionException e) {
            return findStaffById(staffId);
        }
    }

    private Optional<InsightUserRoleVO> findStaffById(long staffId) {
        String sql = "SELECT ur.id ,r.level as roleLevel,ur.uid as staffId,ur.adminId,r.id as roleId,r.value as roleValue" +
                ",u.name as employeeName " +
                "FROM internal_user_role ur  " +
                "  LEFT JOIN internal_role r ON ur.rid = r.id " +
                "  LEFT JOIN internal_user u ON ur.uid = u.id " +
                "WHERE ur.uid = ? AND r.department = ?";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(InsightUserRoleVO.class), staffId, MARKET_DEPARTMENT_ID)
                .stream()
                .min(Comparator.comparing(InsightUserRoleVO::getRoleLevel));
    }

    public Optional<InsightUserRoleVO> findStaffByEmail(String email) {
        String sql = "SELECT ur.id,r.level as roleLevel,ur.uid as staffId,ur.adminId,r.value as roleValue  " +
                "FROM internal_user_role ur  " +
                "  LEFT JOIN internal_user u ON ur.uid = u.id  " +
                "  LEFT JOIN internal_role r ON ur.rid = r.id  " +
                "WHERE u.email = ? AND r.department = ?";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(InsightUserRoleVO.class), email, MARKET_DEPARTMENT_ID)
                .stream()
                .min(Comparator.comparing(InsightUserRoleVO::getRoleLevel));
    }

    public List<InsightUserRoleVO> findUserRolesOf(long staffId) {
        return this.findUserRolesFromDB(staffId);
    }

    private List<InsightUserRoleVO> findUserRolesFromDB(long staffId) {
        String sql = "select r.level as roleLevel,r.department as roleDepartment " +
                "from internal.internal_user_role ur  " +
                "LEFT JOIN internal_role r ON ur.rid = r.id  " +
                "WHERE department = ? AND uid = ?";
        List<InsightUserRoleVO> userRoles = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(InsightUserRoleVO.class),
                MARKET_DEPARTMENT_ID, staffId);

        if (isEmpty(userRoles)) {
            throw InsightExceptions.targetNotFoundExceptionSupplier.get();
        }

        Integer minLevel = userRoles.stream()
                .min(Comparator.comparing(InsightUserRoleVO::getRoleLevel))
                .orElseThrow(ServiceException::new).getRoleLevel();

        sql = "select id as roleId,displayName as roleDisplayName,level as roleLevel from internal_role where level > ? ";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(InsightUserRoleVO.class), minLevel);
    }

    public void updateSystemRolesOf(InsightUserRoleVO target, Long newRoleId) {
        String sql = "update internal_user_role set rid = ? where id = ? ";
        jdbcTemplate.update(sql, newRoleId, target.getId());
    }

    public List<StaffAdminRoleVO> findStaffsByRoleId(long staffId, long roleId) {
        String sql = "SELECT   " +
                "  uid as staffId,u.email,u.name as employeeName,u.tel as mobile,  " +
                "  rid  as roleId " +
                "FROM internal_user_role ur   " +
                "  LEFT JOIN internal_user u ON ur.uid = u.id   " +
                "  LEFT JOIN internal_role r ON ur.rid = r.id   " +
                "where rid = ? and ur.adminId = ? and r.department = ? ";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(StaffAdminRoleVO.class), roleId, staffId, MARKET_DEPARTMENT_ID);
    }

    private static Cache<String, List<InsightUserRoleVO>> ALL_STAFF_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(8, TimeUnit.HOURS)
            .build();

    public List<InsightUserRoleVO> findAllStaffs() {
        try {
            return ALL_STAFF_CACHE.get("ALL_STAFFS_KEY", this::findMarketStaffs);
        } catch (ExecutionException e) {
            return this.findMarketStaffs();
        }
    }

    private List<InsightUserRoleVO> findMarketStaffs() {
        String sql = "SELECT   " +
                "  uid as staffId ,u.email ,u.name as employeeName ," +
                "  u.tel as mobile,rid as roleId ,ur.adminId,r.value as roleValue,r.level as roleLevel " +
                "FROM internal_user_role ur   " +
                "  LEFT JOIN internal_user u ON ur.uid = u.id   " +
                "  LEFT JOIN internal_role r ON ur.rid = r.id   " +
                "where r.department = ? ";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(InsightUserRoleVO.class), MARKET_DEPARTMENT_ID);
    }
}
