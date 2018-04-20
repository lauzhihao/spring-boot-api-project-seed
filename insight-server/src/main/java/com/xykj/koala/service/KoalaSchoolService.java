package com.xykj.koala.service;
import com.xykj.koala.model.KoalaSchool;
import com.xykj.koala.core.Service;


/**
* Created by @author CodeGenerator on @date 2018/04/18.
 */
public interface KoalaSchoolService extends Service<KoalaSchool> {

    KoalaSchool findBySchoolId(Long schoolId);
}
