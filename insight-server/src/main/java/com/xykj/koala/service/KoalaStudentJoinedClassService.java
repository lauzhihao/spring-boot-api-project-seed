package com.xykj.koala.service;
import com.xykj.koala.core.Service;
import com.xykj.koala.model.KoalaStudentJoinedClass;

import java.util.List;


/**
* Created by @author CodeGenerator on @date 2018/04/14.
 */
public interface KoalaStudentJoinedClassService extends Service<KoalaStudentJoinedClass> {

    void remove(Long userId, Long classId);

    void saveOrUpdate(List<KoalaStudentJoinedClass> collect);
}
