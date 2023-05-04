package com.vish.game.tictaktoe.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsTags(@Value("${spring.application.name}") String applicationName) {
        return registry -> registry
                .config()
                .commonTags("application_name", applicationName);
    }
}
