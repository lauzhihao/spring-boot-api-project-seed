package com.xykj.koala.service;
import com.xykj.koala.core.Service;
import com.xykj.koala.model.KoalaClass;

import java.util.List;


/**
* Created by @author CodeGenerator on @date 2018/04/14.
 */
public interface KoalaClassService extends Service<KoalaClass> {

    void saveOrUpdateClasses(List<KoalaClass> koalaClasses);

    void updateActualQuantityOf(long classId, int quantity);

    KoalaClass findByClassId(Long classId);
}
