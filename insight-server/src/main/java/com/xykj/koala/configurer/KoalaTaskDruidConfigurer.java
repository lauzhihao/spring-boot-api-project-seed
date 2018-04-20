package com.xykj.koala.configurer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author liuzhihao
 * @date 2018/4/15
 */
@Configuration
public class KoalaTaskDruidConfigurer extends AbstractDataSourceConfigurer {

    @Value("${koala.task.datasource.druid.username}")
    private String username;

    @Value("${koala.task.datasource.druid.password}")
    private String password;

    @Value("${koala.task.datasource.druid.url}")
    private String url;

    @Bean("koalaTaskJdbcTemplate")
    public JdbcTemplate koalaUserJdbcTemplate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(super.createDataSource(url, username, password));
        return jdbcTemplate;
    }

}