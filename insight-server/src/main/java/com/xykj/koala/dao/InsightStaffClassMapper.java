package com.xykj.koala.dao;

import com.xykj.koala.core.Mapper;
import com.xykj.koala.model.InsightStaffClass;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

/**
 * @author liuzhihao
 */
public interface InsightStaffClassMapper extends Mapper<InsightStaffClass> {

    @Insert("INSERT INTO insight_staff_class (staff_id, school_id, class_id)   " +
            "  SELECT   " +
            "    #{arg0}," +
            "    school_id,   " +
            "    class_id   " +
            "  FROM koala_class   " +
            "  WHERE school_id = #{arg1}")
    void insertBindings(long staffId, long schoolId);

    @Delete("delete from insight_staff_class where staff_id = #{arg0} and school_id = #{arg1}")
    void removeBindings(long staffId, long schoolId);

    @Insert("INSERT INTO insight_staff_class (staff_id, school_id, class_id)   " +
            "  SELECT   " +
            "    #{arg0}," +
            "    school_id,   " +
            "    #{arg1}   " +
            "  FROM koala_class   " +
            "  WHERE class_id = #{arg1}")
    void insertBinding(long staffId, long classId);

}