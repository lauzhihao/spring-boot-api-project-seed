package com.xykj.koala.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xykj.koala.core.AbstractService;
import com.xykj.koala.dao.KoalaSchoolMapper;
import com.xykj.koala.model.KoalaSchool;
import com.xykj.koala.service.KoalaSchoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


/**
 * Created by @author CodeGenerator on @date 2018/04/18.
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class KoalaSchoolServiceImpl extends AbstractService<KoalaSchool> implements KoalaSchoolService {


    @Resource
    private KoalaSchoolMapper koalaSchoolMapper;

    private static final Cache<String, KoalaSchool> SCHOOL_CACHE = CacheBuilder.<String, KoalaSchool>newBuilder().concurrencyLevel(5).build();

    @Override
    public KoalaSchool findBySchoolId(Long schoolId) {
        try {
            return SCHOOL_CACHE.get("SCHOOL_" + schoolId, () -> this.find(schoolId).orElse(KoalaSchool.createDefault()));
        } catch (ExecutionException e) {
            e.printStackTrace();
            log.error(e.toString());
            return this.find(schoolId).orElse(KoalaSchool.createDefault());
        }
    }

    private Optional<KoalaSchool> find(Long schoolId) {

        return Optional.of(koalaSchoolMapper.selectBySchoolId(schoolId));
    }
}
