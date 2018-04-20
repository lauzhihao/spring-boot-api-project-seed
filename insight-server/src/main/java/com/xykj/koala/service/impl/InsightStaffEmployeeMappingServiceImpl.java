package com.xykj.koala.service.impl;

import com.xykj.koala.dao.InsightStaffEmployeeMappingMapper;
import com.xykj.koala.model.InsightStaffEmployeeMapping;
import com.xykj.koala.service.InsightStaffEmployeeMappingService;
import com.xykj.koala.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by @author CodeGenerator on @date 2018/04/14.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InsightStaffEmployeeMappingServiceImpl extends AbstractService<InsightStaffEmployeeMapping> implements InsightStaffEmployeeMappingService {
    @Resource
    private InsightStaffEmployeeMappingMapper insightStaffEmployeeMappingMapper;

}
