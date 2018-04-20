package com.xykj.koala.model;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author liuzhihao
 * @date 2018/4/15
 */
@Data
@Table(name = "insight_dic")
public class InsightDic {

    @Id
    private Long id;

    private String propertyKey;

    private String propertyValue;

}
