package com.xykj.koala.service;
import com.xykj.koala.model.InsightStaffRegion;
import com.xykj.koala.core.Service;


/**
* Created by @author CodeGenerator on @date 2018/04/16.
 */
public interface InsightStaffRegionService extends Service<InsightStaffRegion> {

    void deleteRegionsOf(Long staffId);
}
