package com.xykj.koala.dao;

import com.xykj.koala.core.Mapper;
import com.xykj.koala.model.KoalaStudentJoinedClass;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface KoalaStudentJoinedClassMapper extends Mapper<KoalaStudentJoinedClass> {

    @Update("<script>" +
            "INSERT INTO koala_student_joined_class(class_id,student_id)" +
            "VALUES" +
            "<foreach item='item' collection='list' separator=',' open=' ' close=' ' index=''> " +
            "(#{item.classId},#{item.studentId})  " +
            "</foreach> " +
            "on duplicate key update class_id = class_id " +
            "</script>")
    void insertOrUpdate(@Param("list") List<KoalaStudentJoinedClass> collect);
}