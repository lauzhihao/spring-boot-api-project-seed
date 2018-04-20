package com.xykj.koala.dao;

import com.xykj.koala.core.Mapper;
import com.xykj.koala.model.KoalaSchool;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface KoalaSchoolMapper extends Mapper<KoalaSchool> {

    @Select("select distinct sc.school_id as schoolId ,school_name as schoolName " +
            "from koala_class sc " +
            "LEFT JOIN koala_school s on sc.school_id = s.school_id " +
            "where sc.district_id = #{arg0}")
    List<KoalaSchool> selectByDistrictId(long districtId);

    @Update("<script>" +
            "insert into koala_school(school_id,school_name)" +
            "values" +
            "<foreach item='item' collection='schools' separator=',' open=' ' close=' ' index=''> " +
            "(#{item.schoolId},#{item.schoolName})" +
            "</foreach>" +
            "on duplicate key update school_id = school_id " +
            "</script>")
    void saveOrUpdate(@Param("schools") List<KoalaSchool> schools);

    @Select("select school_id as schoolId,school_name as schoolName from koala_school where school_id = #{arg0}")
    KoalaSchool selectBySchoolId(Long schoolId);

}