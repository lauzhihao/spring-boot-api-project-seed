package com.xykj.koala.utils;

import org.springframework.util.CollectionUtils;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author liuzhihao
 * @date 2018/4/18
 */
public class FormatUtils {

    public static String computeRatio(int molecule, int denominator) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        return denominator == 0 ? "-" : numberFormat.format((float) molecule / (float) denominator * 100) + "%";
    }

    public static String collectionToString(Collection<?> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return "";
        }
        return Arrays.toString(collection.toArray()).replace("[", "").replace("]", "");
    }
}
