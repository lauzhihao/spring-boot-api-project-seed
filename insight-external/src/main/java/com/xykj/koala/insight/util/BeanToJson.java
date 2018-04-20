package com.xykj.koala.insight.util;

/**
 * @author liuzhihao
 * @date 2018/4/10
 */
public interface BeanToJson {

    /**
     * 获取this的JSON字符串
     *
     * @return json字符串
     */
    default String toJson() {
        return JsonUtils.toJson(this);
    }
}
