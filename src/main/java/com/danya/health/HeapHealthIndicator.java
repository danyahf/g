package com.danya.health;


import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Map;

@Component("heapMemory")
@ConditionalOnProperty(prefix = "management.health.heap", name = "enabled", havingValue = "true")
public class HeapHealthIndicator implements HealthIndicator {

    private static final double HEAP_USAGE_WARNING_THRESHOLD = 0.80;
    private static final double HEAP_USAGE_CRITICAL_THRESHOLD = 0.90;

    @Override
    public Health health() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memoryBean.getHeapMemoryUsage();

        long used = heap.getUsed();
        long max = heap.getMax();
        double usageRatio = (double) used / max;

        Map<String, Object> details = new HashMap<>();
        details.put("heapUsedMB", used / (1024 * 1024));
        details.put("heapMaxMB", max / (1024 * 1024));
        details.put("heapUsagePercent", Math.round(usageRatio * 100));

        if (usageRatio > HEAP_USAGE_CRITICAL_THRESHOLD) {
            return Health.down().withDetails(details).withDetail("reason", "High memory usage or GC pressure").build();
        } else if (usageRatio > HEAP_USAGE_WARNING_THRESHOLD) {
            return Health.status("DEGRADED").withDetails(details).withDetail("reason", "Elevated memory usage").build();
        } else {
            return Health.up().withDetails(details).build();
        }
    }
}
