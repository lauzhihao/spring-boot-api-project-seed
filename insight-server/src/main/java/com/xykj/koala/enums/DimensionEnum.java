package com.xykj.koala.enums;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author liuzhihao
 * @date 2018/4/17
 */
public enum DimensionEnum {

    ROLE {
        @Resource
        private JdbcTemplate jdbcTemplate;

        @Override
        public Map<String, Object> query(long staffId, String beginDate, String endDate, Long roleId) {
            System.out.println(jdbcTemplate == null);
            return null;
        }
    },
    PROVINCE,
    CITY,
    DISTRICT,
    SCHOOL,
    CLASS,
    CLASS_DETAIL;


    public Map<String, Object> query(long staffId, String beginDate, String endDate, Long roleId) {
        return null;
    }
}
