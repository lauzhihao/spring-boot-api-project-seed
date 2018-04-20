package com.xykj.koala.task;

import com.xykj.koala.dao.InsightDicMapper;
import com.xykj.koala.enums.StudentTaskTypeEnum;
import com.xykj.koala.model.KoalaStudentTaskRecord;
import com.xykj.koala.service.KoalaStudentTaskRecordService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author liuzhihao
 * @date 2018/4/17
 */
@Component
public class SynchronizeKoalaExamTask {

    @Resource
    @Qualifier("koalaTaskJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Resource
    private KoalaStudentTaskRecordService koalaStudentTaskRecordService;

    @Resource
    private InsightDicMapper insightDicMapper;

    @Scheduled(fixedDelay = 500)
    public void bookTask() {
        String key = "SynchronizeKoalaExamTask.bookTask.processedMaxId";
        Long maxId = insightDicMapper.selectValueBy(key);
        String sql = "select id,studentId,created as submitTime from book_student_task " +
                "where id > ? and created >= ? and deleted = 0 and `status` = 1 order by id limit 500";

        List<KoalaStudentTaskRecord> records = jdbcTemplate.query(sql, (rs, rowNum) -> {
            KoalaStudentTaskRecord koalaStudentTaskRecord = new KoalaStudentTaskRecord();
            koalaStudentTaskRecord.setId(rs.getLong("id"));
            koalaStudentTaskRecord.setSubmitTime(new Date(rs.getLong("submitTime")));
            koalaStudentTaskRecord.setCreateTime(new Date(System.currentTimeMillis()));
            koalaStudentTaskRecord.setErScore(0);
            koalaStudentTaskRecord.setStudentId(rs.getLong("studentId"));
            koalaStudentTaskRecord.setTaskType(StudentTaskTypeEnum.BOOK_TASK.name());
            return koalaStudentTaskRecord;
        }, maxId, LocalDateTime.of(2018, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.of("+8")).toEpochMilli());

        if (isEmpty(records)) {
            return;
        }
        koalaStudentTaskRecordService.save(records);

        insightDicMapper.updateValueOf(key, records.stream().map(KoalaStudentTaskRecord::getId).max(Long::compareTo).orElse(0L));
    }

    @Scheduled(fixedDelay = 500)
    public void erTask() {
        String key = "SynchronizeKoalaExamTask.erTask.processedMaxId";
        Long maxId = insightDicMapper.selectValueBy(key);
        String sql = "select id,studentId,completeTime as submitTime,score from er_student_task " +
                "where id > ? and completeTime > ? and deleted = 0 and `status` = 2 order by id limit 500";

        List<KoalaStudentTaskRecord> records = jdbcTemplate.query(sql, (rs, rowNum) -> {
            KoalaStudentTaskRecord koalaStudentTaskRecord = new KoalaStudentTaskRecord();
            koalaStudentTaskRecord.setId(rs.getLong("id"));
            koalaStudentTaskRecord.setSubmitTime(new Date(rs.getLong("submitTime")));
            koalaStudentTaskRecord.setCreateTime(new Date(System.currentTimeMillis()));
            koalaStudentTaskRecord.setErScore(rs.getInt("score"));
            koalaStudentTaskRecord.setStudentId(rs.getLong("studentId"));
            koalaStudentTaskRecord.setTaskType(StudentTaskTypeEnum.ER_TASK.name());
            return koalaStudentTaskRecord;
        }, maxId, LocalDateTime.of(2018, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.of("+8")).toEpochMilli());

        if (isEmpty(records)) {
            return;
        }
        koalaStudentTaskRecordService.save(records);

        insightDicMapper.updateValueOf(key, records.stream().map(KoalaStudentTaskRecord::getId).max(Long::compareTo).orElse(0L));
    }

    @Scheduled(fixedDelay = 500)
    public void stepTask() {
        String key = "SynchronizeKoalaExamTask.stepTask.processedMaxId";
        Long maxId = insightDicMapper.selectValueBy(key);
        String sql = "select id,userId as studentId,updateTime as submitTime from step_task " +
                "where id > ? and updateTime > ? and deleted = 0 and `status` = 2 order by id limit 500";

        List<KoalaStudentTaskRecord> records = jdbcTemplate.query(sql, (rs, rowNum) -> {
            KoalaStudentTaskRecord koalaStudentTaskRecord = new KoalaStudentTaskRecord();
            koalaStudentTaskRecord.setId(rs.getLong("id"));
            koalaStudentTaskRecord.setSubmitTime(new Date(rs.getLong("submitTime")));
            koalaStudentTaskRecord.setCreateTime(new Date(System.currentTimeMillis()));
            koalaStudentTaskRecord.setErScore(0);
            koalaStudentTaskRecord.setStudentId(rs.getLong("studentId"));
            koalaStudentTaskRecord.setTaskType(StudentTaskTypeEnum.STEP_TASK.name());
            return koalaStudentTaskRecord;
        }, maxId, LocalDateTime.of(2018, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.of("+8")).toEpochMilli());

        if (isEmpty(records)) {
            return;
        }
        koalaStudentTaskRecordService.save(records);

        insightDicMapper.updateValueOf(key, records.stream().map(KoalaStudentTaskRecord::getId).max(Long::compareTo).orElse(0L));
    }

}
