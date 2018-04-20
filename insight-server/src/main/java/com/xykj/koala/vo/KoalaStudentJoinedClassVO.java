package com.xykj.koala.vo;

import lombok.Data;

/**
 * @author liuzhihao
 * @date 2018/4/15
 */
@Data
public class KoalaStudentJoinedClassVO {

    private Long studentId;

    private Long teacherId;

    private Long classId;

    private Long schoolId;

    private Integer grade;

    private Integer classNum;

    private String addrCode;

    private String schoolName;

    private Long createTime;

    private String teacherName;
}
