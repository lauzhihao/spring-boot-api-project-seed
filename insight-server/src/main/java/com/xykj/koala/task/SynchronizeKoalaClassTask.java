package com.xykj.koala.task;

import com.xykj.koala.dao.InsightDicMapper;
import com.xykj.koala.dao.KoalaSchoolMapper;
import com.xykj.koala.model.KoalaClass;
import com.xykj.koala.model.KoalaSchool;
import com.xykj.koala.model.KoalaStudentJoinedClass;
import com.xykj.koala.service.KoalaClassService;
import com.xykj.koala.service.KoalaStudentJoinedClassService;
import com.xykj.koala.utils.RegionUtils;
import com.xykj.koala.vo.KoalaStudentJoinedClassVO;
import com.xykj.koala.vo.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * 在koala数据库中同步学校班级的基础信息任务
 *
 * @author liuzhihao
 * @date 2018/4/15
 */
@Component
@Slf4j
public class SynchronizeKoalaClassTask {

    @Resource
    @Qualifier("koalaUserJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Resource
    private InsightDicMapper insightDicMapper;

    @Resource
    private KoalaStudentJoinedClassService koalaStudentJoinedClassService;

    @Resource
    private KoalaClassService koalaClassService;

    @Scheduled(fixedDelay = 500)
    public void syncClassInfo() {
        String key = "SynchronizeKoalaClassTask.syncClassInfo.processedMaxId";
        Long processedMaxId = insightDicMapper.selectValueBy(key);

        String sql = "SELECT " +
                "  ur.userId     AS studentId, " +
                "  ur.resourceId AS classId, " +
                "  cr.teacherId, " +
                "  cr.schoolId, " +
                "  cr.teacherName," +
                "  cr.grade, " +
                "  cr.classNum, " +
                "  cr.addrCode," +
                "  cr.schoolName, " +
                "  cr.createTime " +
                "FROM ( " +
                "       SELECT " +
                "         userId, " +
                "         resourceId " +
                "       FROM auth " +
                "       WHERE authType = 2 AND resourceType = 3 " +
                "     ) ur " +
                "  LEFT JOIN " +
                "  ( " +
                "    SELECT " +
                "      a.userId AS teacherId, " +
                "      u.name   as teacherName," +
                "      c.id     AS classId, " +
                "      c.grade, " +
                "      c.number AS classNum, " +
                "      c.createTime, " +
                "      s.id     AS schoolId, " +
                "      s.addrCode," +
                "      s.name as schoolName " +
                "    FROM school_class c " +
                "      LEFT JOIN school s ON c.schoolId = s.id " +
                "      INNER JOIN auth a ON (c.id = a.resourceId AND resourceType = 3 AND authType = 1)" +
                "      LEFT JOIN user u on a.userId = u.id " +
                "    WHERE c.status = 1 AND blockingStatus = 1 " +
                "  ) cr ON ur.resourceId = cr.classId " +
                "WHERE cr.classId > 0 and ur.userId > ? " +
                "order by studentId limit 500";
        List<KoalaStudentJoinedClassVO> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(KoalaStudentJoinedClassVO.class), processedMaxId);
        if (isEmpty(result)) {
            return;
        }

        koalaStudentJoinedClassService.saveOrUpdate(
                result.stream()
                        .map(r ->
                                KoalaStudentJoinedClass
                                        .builder()
                                        .studentId(r.getStudentId())
                                        .createTime(new Date(r.getCreateTime()))
                                        .classId(r.getClassId())
                                        .build())
                        .collect(Collectors.toList())
        );

        List<KoalaClass> koalaClasses = result.stream()
                .map(r -> {
                    Region region = RegionUtils.from(r.getAddrCode());

                    KoalaClass koalaClass = new KoalaClass();
                    BeanUtils.copyProperties(r, koalaClass);
                    koalaClass.setCreateTime(new Date(r.getCreateTime()));
                    koalaClass.setClassCode(String.format(r.getGrade() + "%02d", r.getClassNum()));
                    koalaClass.setClassName(String.format(r.getGrade() + "级%s班", r.getClassNum()));
                    koalaClass.setCountryId(region.getCountryId());
                    koalaClass.setProvinceId(region.getProvinceId());
                    koalaClass.setCityId(region.getCityId());
                    koalaClass.setDistrictId(region.getDistrictId());
                    koalaClass.setTeacherName(r.getTeacherName());

                    return koalaClass;
                })
                .collect(Collectors.toList());

        if (!isEmpty(koalaClasses)) {
            koalaClassService.saveOrUpdateClasses(koalaClasses);
        }

        Map<Long, List<KoalaStudentJoinedClassVO>> collect = result.parallelStream().collect(Collectors.groupingBy(KoalaStudentJoinedClassVO::getSchoolId));
        List<KoalaSchool> schools = collect.entrySet().parallelStream()
                .map(e -> {
                    KoalaSchool koalaSchool = KoalaSchool.createDefault();
                    koalaSchool.setSchoolId(e.getKey());
                    e.getValue().stream().findFirst().ifPresent(s -> koalaSchool.setSchoolName(s.getSchoolName()));

                    return koalaSchool;
                }).collect(Collectors.toList());
        if (!isEmpty(schools)) {
            koalaSchoolMapper.saveOrUpdate(schools);
        }
        insightDicMapper.updateValueOf(key, result.stream().mapToLong(KoalaStudentJoinedClassVO::getStudentId).max().orElse(0L));
    }

    @Resource
    private KoalaSchoolMapper koalaSchoolMapper;
}
