package com.xykj.koala.service.impl;

import com.xykj.koala.core.AbstractService;
import com.xykj.koala.dao.KoalaStudentJoinedClassMapper;
import com.xykj.koala.model.KoalaStudentJoinedClass;
import com.xykj.koala.service.KoalaStudentJoinedClassService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;


/**
 * Created by @author CodeGenerator on @date 2018/04/14.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class KoalaStudentJoinedClassServiceImpl extends AbstractService<KoalaStudentJoinedClass> implements KoalaStudentJoinedClassService {
    @Resource
    private KoalaStudentJoinedClassMapper koalaStudentJoinedClassMapper;

    @Override
    public void remove(Long userId, Long classId) {
        Condition condition = new Condition(KoalaStudentJoinedClass.class);
        condition.and().andEqualTo("userId", userId).andEqualTo("classId", classId);
        koalaStudentJoinedClassMapper.deleteByCondition(condition);
    }

    @Override
    public void saveOrUpdate(List<KoalaStudentJoinedClass> collect) {
        if (isEmpty(collect)) {
            return;
        }
        koalaStudentJoinedClassMapper.insertOrUpdate(collect);
    }
}
