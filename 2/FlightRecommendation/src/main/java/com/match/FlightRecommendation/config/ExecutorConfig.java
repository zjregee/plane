package com.match.FlightRecommendation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ExecutorConfig {
    public final static Logger logger = LoggerFactory.getLogger(ExecutorConfig.class);

    private final int queueCapacity = 1000;
    private int corePoolSize;
    private int maxPoolSize;
    private final int keepAliveSeconds = 30;

    @Bean("asyncServiceExecutor")
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        maxPoolSize = Runtime.getRuntime().availableProcessors();
        corePoolSize = maxPoolSize;
        logger.info(" " + maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setCorePoolSize(corePoolSize);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.initialize();
        return executor;
    }
}
