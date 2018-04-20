package com.xykj.koala.core;

import com.google.common.collect.Maps;
import com.xykj.koala.vo.InsightUserRoleVO;

import java.util.Map;

/**
 * @author liuzhihao
 * @date 2018/4/16
 */
public class InsightSystemContext {

    private static final ThreadLocal<Map<String, Object>> LOCAL_THREAD_MAP = ThreadLocal.withInitial(Maps::newHashMap);

    private static final String STAFF_KEY = "insightStaff";

    public static void putInsightStaff(InsightUserRoleVO insightUserRoleVO) {
        LOCAL_THREAD_MAP.get().put(STAFF_KEY, insightUserRoleVO);
    }

    public static InsightUserRoleVO getInsightStaff() {
        Object obj = LOCAL_THREAD_MAP.get().get(STAFF_KEY);
        return (InsightUserRoleVO) obj;
    }
}
