package com.xykj.koala.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author liuzhihao
 */
@Data
@Builder
@Table(name = "koala_student_joined_class")
public class KoalaStudentJoinedClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "create_time")
    private Date createTime;
}