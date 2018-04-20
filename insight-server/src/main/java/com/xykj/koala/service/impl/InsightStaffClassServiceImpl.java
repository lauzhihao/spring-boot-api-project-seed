package com.xykj.koala.service.impl;

import com.xykj.koala.core.AbstractService;
import com.xykj.koala.dao.InsightStaffClassMapper;
import com.xykj.koala.model.InsightStaffClass;
import com.xykj.koala.service.InsightStaffClassService;
import com.xykj.koala.service.KoalaClassService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;

import static com.xykj.koala.service.impl.InsightStatisticsService.TODAY_STATISTICS_RESULT;


/**
 * Created by @author CodeGenerator on @date 2018/04/18.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InsightStaffClassServiceImpl extends AbstractService<InsightStaffClass> implements InsightStaffClassService {

    @Resource
    private InsightStaffClassMapper insightStaffClassMapper;

    @Resource
    private KoalaClassService koalaClassService;

    @Override
    public void createSchoolBinding(long staffId, long schoolId) {
        //把该学校的所有班级写入
        insightStaffClassMapper.insertBindings(staffId, schoolId);

        TODAY_STATISTICS_RESULT.cleanUp();
    }

    @Override
    public void removeSchoolBinding(long staffId, long schoolId) {
        insightStaffClassMapper.removeBindings(staffId, schoolId);

        TODAY_STATISTICS_RESULT.cleanUp();

    }

    @Override
    public void updateClassBinding(long staffId, long classId, int quantity) {
        koalaClassService.updateActualQuantityOf(classId, quantity);
        deleteClassBinding(staffId, classId);

        insightStaffClassMapper.insertBinding(staffId, classId);

        TODAY_STATISTICS_RESULT.cleanUp();
    }

    @Override
    public void deleteClassBinding(long staffId, long classId) {
        Condition condition = new Condition(InsightStaffClass.class);
        condition.and().andEqualTo("staffId", staffId)
                .andEqualTo("classId", classId);
        insightStaffClassMapper.deleteByCondition(condition);

        TODAY_STATISTICS_RESULT.cleanUp();

    }
}
