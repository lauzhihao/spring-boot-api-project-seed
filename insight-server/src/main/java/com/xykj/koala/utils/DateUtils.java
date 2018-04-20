package com.xykj.koala.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author liuzhihao
 * @date 2018/4/18
 */
public class DateUtils {

    public static LocalDate computeTheLastDayOfLastMonth(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusMonths(1).withDayOfMonth(1).minusDays(1);
    }
}
