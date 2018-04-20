package com.xykj.koala.dao;

import com.xykj.koala.core.Mapper;
import com.xykj.koala.model.InsightDic;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author liuzhihao
 * @date 2018/4/15
 */
public interface InsightDicMapper extends Mapper<InsightDic> {

    @Select("select property_value from insight_dic where property_key = #{0}")
    Long selectValueBy(String key);

    @Update("update insight_dic set property_value = #{param2} where property_key = #{param1} ")
    void updateValueOf(String key, long value);
}
