package com.xykj.koala.service.impl;

import com.xykj.koala.dao.KoalaStudentMapper;
import com.xykj.koala.model.KoalaStudent;
import com.xykj.koala.service.KoalaStudentService;
import com.xykj.koala.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by @author CodeGenerator on @date 2018/04/14.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class KoalaStudentServiceImpl extends AbstractService<KoalaStudent> implements KoalaStudentService {
    @Resource
    private KoalaStudentMapper koalaStudentMapper;

}
