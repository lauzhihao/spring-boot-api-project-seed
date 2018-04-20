package com.xykj.koala.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "insight_staff_class")
public class InsightStaffClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "staff_id")
    private Long staffId;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "school_id")
    private Long schoolId;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return staff_id
     */
    public Long getStaffId() {
        return staffId;
    }

    /**
     * @param staffId
     */
    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    /**
     * @return class_id
     */
    public Long getClassId() {
        return classId;
    }

    /**
     * @param classId
     */
    public void setClassId(Long classId) {
        this.classId = classId;
    }

    /**
     * @return school_id
     */
    public Long getSchoolId() {
        return schoolId;
    }

    /**
     * @param schoolId
     */
    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}