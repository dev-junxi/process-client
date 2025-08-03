package com.susu.processclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@Slf4j
@EnableScheduling
//@RefreshScope
public class ProcessorConfiguration {

    @Value("${process.log.level:info}")
    private String level;

    @Bean
//    @RefreshScope
    public ProcessClient processClient() {
        log.info("processClient init");
        ProcessClient processClient = ProcessClient.builder()
                .defaultLogLevel(level)
                .build();
        ProcessClientHolder.set(processClient);
        return processClient;
    }

    @Bean
    public SpringApplicationContextHolder springApplicationContextHolder() {
        return new SpringApplicationContextHolder();
    }

    @Bean
    public TableInitializer tableInitializer(DataSource dataSource) {
        return new TableInitializer(dataSource);
    }

    @Bean
    public ProcessSnapshotCleanupTask processSnapshotCleanupTask(DataSource dataSource) {
        return new ProcessSnapshotCleanupTask(dataSource);
    }

    @Bean
//    @ConditionalOnClass(Filter.class)
    public FilterRegistrationBean<ChainIdFilter> chainIdFilter() {
        FilterRegistrationBean<ChainIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ChainIdFilter());
        registrationBean.setUrlPatterns(List.of("/*"));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    @Bean
//    @ConditionalOnClass(RequestInterceptor.class)
    public ChainIdFeignInterceptor chainIdFeignInterceptor() {
        return new ChainIdFeignInterceptor();
    }


}
