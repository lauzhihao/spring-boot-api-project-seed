package com.xykj.koala.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author liuzhihao
 */
@Table(name = "koala_class")
@Data
public class KoalaClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "class_code")
    private String classCode;

    @Column(name = "class_name")
    private String className;

    private Integer grade;

    @Column(name = "school_id")
    private Long schoolId;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "province_id")
    private Long provinceId;

    @Column(name = "country_id")
    private Long countryId;

    @Column(name = "joined_student_quantity")
    private Integer joinedStudentQuantity;

    @Column(name = "actual_student_quantity")
    private Integer actualStudentQuantity;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "teacher_name")
    private String teacherName;
}