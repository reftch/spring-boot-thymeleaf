package com.chornyi.spring.boot.thymeleaf.config;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.StopWatch;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class DatabaseConfiguration {

    private final Environment env;

    public DatabaseConfiguration(Environment env) {
        this.env = env;
    }

    @Bean
    public SpringLiquibase liquibase(@Qualifier("taskExecutor") TaskExecutor taskExecutor,
                                     DataSource dataSource, LiquibaseProperties liquibaseProperties) {

        SpringLiquibase liquibase = new AsyncSpringLiquibase(taskExecutor, env);
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:config/liquibase/master.xml");
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        liquibase.setShouldRun(liquibaseProperties.isEnabled());
        log.debug("Configuring Liquibase");
        return liquibase;
    }

    @Slf4j
    public static class AsyncSpringLiquibase extends SpringLiquibase {
        private final TaskExecutor taskExecutor;

        private final Environment env;

        public AsyncSpringLiquibase(@Qualifier("taskExecutor") TaskExecutor taskExecutor, Environment env) {
            this.taskExecutor = taskExecutor;
            this.env = env;
        }

        @Override
        public void afterPropertiesSet() throws LiquibaseException {
            taskExecutor.execute(() -> {
                try {
                    log.warn("Starting Liquibase asynchronously, your database might not be ready at startup!");
                    initDb();
                } catch (LiquibaseException e) {
                    log.error("Liquibase could not start correctly, your database is NOT ready: {}",
                            e.getMessage(), e);
                }
            });
        }

        protected void initDb() throws LiquibaseException {
            StopWatch watch = new StopWatch();
            watch.start();
            super.afterPropertiesSet();
            watch.stop();
            log.info("Liquibase has updated your database in {} ms", watch.getTotalTimeMillis());
            if (watch.getTotalTimeMillis() > 5_000) {
                log.warn("Warning, Liquibase took more than 5 seconds to start up!");
            }
        }
    }

}
