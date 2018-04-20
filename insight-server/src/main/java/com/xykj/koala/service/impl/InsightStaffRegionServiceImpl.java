package com.xykj.koala.service.impl;

import com.xykj.koala.dao.InsightStaffRegionMapper;
import com.xykj.koala.model.InsightStaffRegion;
import com.xykj.koala.service.InsightStaffRegionService;
import com.xykj.koala.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;


/**
 * Created by @author CodeGenerator on @date 2018/04/16.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InsightStaffRegionServiceImpl extends AbstractService<InsightStaffRegion> implements InsightStaffRegionService {
    @Resource
    private InsightStaffRegionMapper insightStaffRegionMapper;

    @Override
    public void deleteRegionsOf(Long staffId) {
        Condition condition = new Condition(InsightStaffRegion.class);
        condition.and().andEqualTo("staffId", staffId);
        insightStaffRegionMapper.deleteByCondition(condition);
    }
}
