package com.xykj.koala.dao;

import com.xykj.koala.core.Mapper;
import com.xykj.koala.model.KoalaClass;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface KoalaClassMapper extends Mapper<KoalaClass> {

    @Update("<script>" +
            "insert into koala_class(class_id,teacher_id,class_code,class_name,grade,school_id,district_id,city_id,province_id,country_id,joined_student_quantity,teacher_name)" +
            "values " +
            "<foreach item='item' collection='koalaClasses' separator=',' open=' ' close=' ' index=''> " +
            "(#{item.classId},#{item.teacherId},#{item.classCode},#{item.className},#{item.grade},#{item.schoolId},#{item.districtId}," +
            "#{item.cityId},#{item.provinceId},#{item.countryId},#{item.joinedStudentQuantity},#{item.teacherName})  " +
            "</foreach> " +
            "on duplicate key update update_time = now() " +
            "</script>")
    void insertOrUpdate(@Param("koalaClasses") List<KoalaClass> koalaClasses);

    @Update("insert into koala_class(class_id,actual_student_quantity)value(#{arg0},#{arg1})" +
            "on duplicate key update actual_student_quantity = #{arg1}")
    void updateActualQuantityOf(long classId, int quantity);

    @Select("select distinct class_id as classId,class_name as className from koala_class where school_id = #{arg0}")
    List<Map<String, Object>> selectForBinding(long schoolId);

}