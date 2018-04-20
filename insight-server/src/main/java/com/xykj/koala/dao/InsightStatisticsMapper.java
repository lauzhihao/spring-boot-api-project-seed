package com.xykj.koala.dao;

import com.xykj.koala.vo.RangeQuantityVO;
import com.xykj.koala.vo.StatisticsChartVO;
import com.xykj.koala.vo.StatisticsResultVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author liuzhihao
 * @date 2018/4/17
 */
public interface InsightStatisticsMapper {

    @Select("SELECT   " +
            "  count(r.student_id)    AS totalQualified   " +
            "FROM (   " +
            "       SELECT   " +
            "         count(DISTINCT DATE_FORMAT(submit_time, '%Y-%m-%d')) AS cnt,   " +
            "         student_id   " +
            "       FROM koala_student_task_record r   " +
            "       WHERE submit_time BETWEEN #{arg1} AND #{arg2}   " +
            "             AND exists(   " +
            "                 SELECT 1   " +
            "                 FROM koala_student_joined_class jc   " +
            "                   LEFT JOIN koala_class kc ON jc.class_id = kc.class_id   " +
            "                 WHERE jc.student_id = r.student_id   " +
            "             )   " +
            "       GROUP BY student_id   " +
            "       HAVING cnt >= 4   " +
            "     ) r")
    Integer selectTotalQualifiedBetween(long staffId, String beginDate, String endDate);

    @Select("select IFNULL(sum(actual_student_quantity),0) from koala_class kc " +
            " WHERE create_time <= #{arg0}")
    Integer selectTotalActualBefore(LocalDate endDate);

    @Select("")
    List<Map<String, Object>> selectStaffsDetailsOf(long staffId);

    @Select("select distinct class_id from koala_class")
    List<Long> selectAllClasses();

    @Insert("INSERT INTO insight_qualified_daily (stat_on, class_id, qualified_quantity)   " +
            "  SELECT   " +
            "    #{arg1},   " +
            "    #{arg0},   " +
            "    count(student_id)   " +
            "  FROM   " +
            "    (   " +
            "      SELECT   " +
            "        count(DISTINCT submit_on) AS days,   " +
            "        r.student_id   " +
            "      FROM koala_student_task_record r   " +
            "        LEFT JOIN koala_student_joined_class j on r.student_id = j.student_id   " +
            "      WHERE submit_time < #{arg1} and j.class_id = #{arg0}   " +
            "      GROUP BY r.student_id   " +
            "      HAVING days >= #{arg2}   " +
            "    ) r1 ")
    void updateQualifiedQuantityOf(Long classId, LocalDate today, int qualifiedDays);

    @Select("SELECT count(student_id) " +
            "FROM " +
            "  koala_student_joined_class j " +
            "WHERE NOT exists( " +
            "    SELECT 1 " +
            "    FROM koala_student_task_record r " +
            "    WHERE r.student_id = j.student_id AND r.submit_on BETWEEN #{arg0} AND #{arg1} " +
            ") " +
            "      AND j.class_id = #{arg2};")
    Integer selectCountForRange1Between(LocalDate begin, LocalDate end, Long classId);


    @Select("SELECT " +
            "  count(student_id) as quantity, " +
            "  days as daysRange " +
            "FROM " +
            "  ( " +
            "    SELECT " +
            "      j.student_id, " +
            "      count(DISTINCT submit_on) AS days " +
            "    FROM koala_student_joined_class j " +
            "      LEFT JOIN koala_student_task_record r ON r.student_id = j.student_id " +
            "    WHERE submit_on BETWEEN #{arg0} AND #{arg1} and j.class_id = #{arg2} " +
            "    GROUP BY j.student_id " +
            "  ) r1 " +
            "GROUP BY days")
    List<RangeQuantityVO> selectSubmitQuantityMap(LocalDate begin, LocalDate end, Long classId);

    @Insert("insert into insight_qualified_daily(stat_on, class_id, quantity_range_1, quantity_range_2, quantity_range_3, quantity_range_4, quantity_range_5) " +
            "VALUES (#{arg0},#{arg1},#{arg2},#{arg3},#{arg4},#{arg5},#{arg6}) ON DUPLICATE KEY UPDATE class_id = class_id")
    void insertDaily(LocalDate end, Long classId, Integer range1, Integer range2, Integer range3, Integer range4, Integer range5);

    @Select("" +
            "SELECT " +
            "  t1 as totalQualified, " +
            "  t2 as totalActual, " +
            "  t3 as totalJoined, " +
            "  r1.staffId " +
            "FROM " +
            "  ( " +
            "    SELECT " +
            "      sum(d.quantity_range_5)         AS t1, " +
            "      sum(kc.actual_student_quantity) AS t2, " +
            "      c.staff_id                      AS staffId " +
            "    FROM insight_staff_class c " +
            "      LEFT JOIN insight_qualified_daily d ON d.class_id = c.class_id " +
            "      LEFT JOIN koala_class kc ON kc.class_id = c.class_id " +
            "    WHERE stat_on = #{arg1} AND staff_id IN (${arg0})" +
            "    GROUP BY c.staff_id " +
            "  ) AS r1 " +
            "  INNER JOIN " +
            "  ( " +
            "    SELECT " +
            "      count(1) AS t3, " +
            "      staff_id AS staffId " +
            "    FROM koala_student_joined_class jc " +
            "      LEFT JOIN insight_staff_class sc ON jc.class_id = sc.class_id " +
            "    WHERE sc.staff_id IN (${arg0})" +
            "    GROUP BY staffId " +
            "  ) AS r2 ON r1.staffId = r2.staffId "
    )
    List<StatisticsResultVO> statisticsFor(String adminsLv3, String endDate);

    @Select("SELECT  " +
            "  s1 as range1,s2 as range2,s3 as range3,s4 as range4,s5 as range5," +
            "r.class_id as classId,sc.school_id as schoolId,sr.district_id as districtId  " +
            "FROM (  " +
            "       SELECT  " +
            "         quantity_range_1 AS s1,  " +
            "         quantity_range_2 AS s2,  " +
            "         quantity_range_3 AS s3,  " +
            "         quantity_range_4 AS s4,  " +
            "         quantity_range_5 AS s5,  " +
            "         kc.class_id  " +
            "       FROM insight_qualified_daily d  " +
            "         LEFT JOIN insight_staff_class kc ON d.class_id = kc.class_id  " +
            "       WHERE d.stat_on = #{arg1}  " +
            "             AND staff_id = #{arg0}  " +
            "     ) r  " +
            "INNER JOIN insight_staff_class sc on r.class_id = sc.class_id  " +
            "INNER JOIN insight_staff_region sr on sc.staff_id = sr.staff_id")
    List<StatisticsChartVO> selectChartsOf(long staffId, String endDate);

    @Select("SELECT  " +
            "         sc.staff_id                     AS staffId,  " +
            "         d.class_id                      AS classId,  " +
            "         kc.school_id                    AS schoolId,  " +
            "         kc.district_id                  AS districtId,  " +
            "         kc.city_id                      AS cityId,  " +
            "         kc.province_id                  AS provinceId,  " +
            "         sum(quantity_range_5)           AS totalQualified,  " +
            "         count(jc.student_id)            AS totalJoined,  " +
            "         sum(kc.actual_student_quantity) AS totalActual  " +
            "       FROM insight_qualified_daily d  " +
            "         INNER JOIN koala_class kc ON d.class_id = kc.class_id  " +
            "         LEFT JOIN koala_student_joined_class jc ON d.class_id = jc.class_id  " +
            "         LEFT JOIN insight_staff_class sc ON d.class_id = sc.class_id  " +
            "       WHERE stat_on = #{arg0}  and sc.staff_id is not null " +
            "       GROUP BY d.class_id,  " +
            "         kc.school_id,  " +
            "         kc.district_id,  " +
            "         kc.city_id,  " +
            "         kc.province_id,  " +
            "         sc.staff_id")
    List<StatisticsResultVO> statisticsToday(String statDate);
}
