package com.xykj.koala.service.impl;

import com.xykj.koala.dao.KoalaStudentTaskRecordMapper;
import com.xykj.koala.model.KoalaStudentTaskRecord;
import com.xykj.koala.service.KoalaStudentTaskRecordService;
import com.xykj.koala.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by @author CodeGenerator on @date 2018/04/14.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class KoalaStudentTaskRecordServiceImpl extends AbstractService<KoalaStudentTaskRecord> implements KoalaStudentTaskRecordService {
    @Resource
    private KoalaStudentTaskRecordMapper koalaStudentTaskRecordMapper;

}
