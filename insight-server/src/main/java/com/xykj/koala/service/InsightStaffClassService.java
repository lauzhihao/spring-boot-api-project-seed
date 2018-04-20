package com.xykj.koala.service;
import com.xykj.koala.model.InsightStaffClass;
import com.xykj.koala.core.Service;


/**
* Created by @author CodeGenerator on @date 2018/04/18.
 */
public interface InsightStaffClassService extends Service<InsightStaffClass> {

    void createSchoolBinding(long staffId, long schoolId);

    void removeSchoolBinding(long staffId, long schoolId);

    void updateClassBinding(long staffId, long classId, int quantity);

    void deleteClassBinding(long staffId, long classId);

}
