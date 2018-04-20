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
public class KoalaInternalDruidConfigurer extends AbstractDataSourceConfigurer {

    @Value("${koala.internal.datasource.druid.username}")
    private String username;

    @Value("${koala.internal.datasource.druid.password}")
    private String password;

    @Value("${koala.internal.datasource.druid.url}")
    private String url;

    @Bean("koalaInternalJdbcTemplate")
    public JdbcTemplate koalaUserJdbcTemplate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(super.createDataSource(url, username, password));
        return jdbcTemplate;
    }

}
