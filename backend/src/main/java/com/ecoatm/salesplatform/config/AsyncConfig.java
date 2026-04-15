package com.ecoatm.salesplatform.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Shared async + retry foundation.
 *
 * <p>Used by {@code @TransactionalEventListener(AFTER_COMMIT)} handlers so side
 * effects (email, Snowflake sync) run off the request thread and cannot roll
 * back the originating business transaction.
 */
@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig {

    public static final String EMAIL_EXECUTOR = "emailExecutor";
    public static final String SNOWFLAKE_EXECUTOR = "snowflakeExecutor";

    @Bean(name = EMAIL_EXECUTOR)
    public Executor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("email-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }

    @Bean(name = SNOWFLAKE_EXECUTOR)
    public Executor snowflakeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("snowflake-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
