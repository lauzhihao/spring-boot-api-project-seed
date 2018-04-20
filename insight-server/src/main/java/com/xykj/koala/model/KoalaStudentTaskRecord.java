package com.xykj.koala.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author liuzhihao
 */
@Data
@Table(name = "koala_student_task_record")
public class KoalaStudentTaskRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "task_type")
    private String taskType;

    @Column(name = "er_score")
    private Integer erScore;

    @Column(name = "submit_time")
    private Date submitTime;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

}