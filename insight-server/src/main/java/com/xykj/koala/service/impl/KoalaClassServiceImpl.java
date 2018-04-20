package com.xykj.koala.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xykj.koala.core.AbstractService;
import com.xykj.koala.dao.KoalaClassMapper;
import com.xykj.koala.model.KoalaClass;
import com.xykj.koala.service.KoalaClassService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.springframework.util.CollectionUtils.isEmpty;


/**
 * Created by @author CodeGenerator on @date 2018/04/14.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class KoalaClassServiceImpl extends AbstractService<KoalaClass> implements KoalaClassService {
    @Resource
    private KoalaClassMapper koalaClassMapper;

    @Override
    public void saveOrUpdateClasses(List<KoalaClass> koalaClasses) {
        if (isEmpty(koalaClasses)) {
            return;
        }
        koalaClassMapper.insertOrUpdate(koalaClasses);

        KOALA_CLASSES_CACHE.cleanUp();
    }

    @Override
    public void updateActualQuantityOf(long classId, int quantity) {
        koalaClassMapper.updateActualQuantityOf(classId, quantity);
    }

    @Override
    public KoalaClass findByClassId(Long classId) {
        try {
            return KOALA_CLASSES_CACHE.get(classId, () -> find(classId));
        } catch (ExecutionException e) {
            e.printStackTrace();
            return find(classId);
        }
    }

    private KoalaClass find(Long classId) {
        Condition condition = new Condition(KoalaClass.class);
        condition.and().andEqualTo("classId", classId);

        return koalaClassMapper.selectByCondition(condition).stream().findFirst().orElseGet(KoalaClass::new);
    }

    private static final Cache<Long, KoalaClass> KOALA_CLASSES_CACHE = CacheBuilder.<Long, KoalaClass>newBuilder().build();
}
