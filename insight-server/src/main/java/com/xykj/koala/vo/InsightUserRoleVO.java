package com.xykj.koala.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Objects;

/**
 * @author liuzhihao
 * @date 2018/4/16
 */
@Data
public class InsightUserRoleVO {

    private static final String SUPER_ADMIN_ROLE_NAME = "admin0Level";

    public static final String ADMIN_LEVEL_1 = "admin1Level";

    public static final String ADMIN_LEVEL_2 = "admin2Level";

    public static final String ADMIN_LEVEL_3 = "admin3Level";

    @JSONField(serialize = false)
    private Long id;

    @JSONField(serialize = false)
    private Long staffId;

    private String employeeName;

    private Long roleId;

    @JSONField(serialize = false)
    private String roleValue;

    @JSONField(serialize = false)
    private Integer roleLevel;

    private String roleDisplayName;

    @JSONField(serialize = false)
    private Long adminId;

    @JSONField(serialize = false)
    public boolean isSuperAdmin() {
        return Objects.equals(this.roleValue, SUPER_ADMIN_ROLE_NAME);
    }
}
