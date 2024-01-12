package com.realtime.protection.configuration.threadpool;

import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.configuration.exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
@EnableAsync
public class OverrideDefaultThreadPoolConfig implements AsyncConfigurer {
    private final TaskThreadPoolConfig taskThreadPoolConfig;
    private final GlobalExceptionHandler globalExceptionHandler;

    public OverrideDefaultThreadPoolConfig(TaskThreadPoolConfig taskThreadPoolConfig,
                                           GlobalExceptionHandler globalExceptionHandler) {
        this.taskThreadPoolConfig = taskThreadPoolConfig;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

        threadPoolTaskExecutor.setCorePoolSize(taskThreadPoolConfig.getCorePoolSize());
        threadPoolTaskExecutor.setMaxPoolSize(taskThreadPoolConfig.getMaxPoolSize());
        threadPoolTaskExecutor.setKeepAliveSeconds(taskThreadPoolConfig.getKeepAliveSeconds());
        threadPoolTaskExecutor.setQueueCapacity(taskThreadPoolConfig.getQueueCapacity());

        threadPoolTaskExecutor.setThreadNamePrefix("ThreadPool-");
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        threadPoolTaskExecutor.initialize();

        return threadPoolTaskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.debug(method.getName() + " meets error: " + ex.getMessage());

            if (ex instanceof DorisStartException) {
                globalExceptionHandler.handleDorisStartException((DorisStartException) ex);
                return;
            }

            globalExceptionHandler.handleGlobalException((Exception) ex);
        };
    }
}
