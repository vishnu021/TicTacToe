package com.vish.game.tictaktoe.metrics;

import io.micrometer.core.instrument.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@AllArgsConstructor
public class Metrics {

    private final MeterRegistry meterRegistry;

    public void record(String metricName, long value, String... tags) {
        try {
            Timer.builder(metricName)
                    .tags(tags)
                    .register(meterRegistry)
                    .record(value, TimeUnit.MICROSECONDS);
        } catch (Exception e) {
            log.error("Failed to record value for metrics : {}", metricName);
        }
    }

    public void increment(String metricName, String... tags) {
        try {
            Counter.builder(metricName)
                    .tags(tags)
                    .register(meterRegistry)
                    .increment();
        } catch (Exception e) {
            log.error("Failed to increment counter for metrics : {}", metricName);
        }
    }

    public void recordDistribution(String metricName, long value, String... tags) {
        try {
            DistributionSummary.builder(metricName)
                    .publishPercentiles(0.5, 0.75, 0.95, 0.99)
                    .tags(tags)
                    .register(meterRegistry)
                    .record(value);
        } catch (Exception e) {
            log.error("Failed to increment counter for metrics : {}", metricName);
        }
    }
}
