package com.vish.game.tictactoe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.util.concurrent.Executors;

/**
 * Configuration for async task execution using Java 21 virtual threads.
 * Virtual threads provide lightweight concurrency for I/O-bound operations.
 */
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Bean
    @Override
    public TaskExecutor getAsyncExecutor() {
        // Use virtual threads for all @Async operations
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
}
