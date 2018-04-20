package com.xykj.koala.configurer;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;

/**
 * @author liuzhihao
 * @date 2018/4/15
 */
public abstract class AbstractDataSourceConfigurer {

    protected DataSource createDataSource(String url, String username, String password) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        return druidDataSource;
    }
}
