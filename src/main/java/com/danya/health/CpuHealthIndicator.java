package com.danya.health;

import com.sun.management.OperatingSystemMXBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.Optional;

@Component
@ConditionalOnProperty(prefix = "management.health.cpu", name = "enabled", havingValue = "true")
public class CpuHealthIndicator implements HealthIndicator {

    private static final DecimalFormat PCT = new DecimalFormat("0.##%");

    private final double warnThreshold;
    private final double downThreshold;

    public CpuHealthIndicator(
            @Value("${management.health.cpu.warn-threshold:0.75}") double warnThreshold,
            @Value("${management.health.cpu.down-threshold:0.90}") double downThreshold) {
        if (warnThreshold < 0 || warnThreshold > 1) {
            throw new IllegalArgumentException("warnThreshold must be between 0 and 1");
        }
        if (downThreshold < 0 || downThreshold > 1) {
            throw new IllegalArgumentException("downThreshold must be between 0 and 1");
        }
        if (warnThreshold >= downThreshold) {
            throw new IllegalArgumentException("warnThreshold must be less than downThreshold");
        }
        this.warnThreshold = warnThreshold;
        this.downThreshold = downThreshold;
    }

    private static Optional<OperatingSystemMXBean> getPlatformOsMxBean() {
        try {
            OperatingSystemMXBean bean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            return Optional.ofNullable(bean);
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    private static double round(double v) {
        return Math.round(v * 10000d) / 10000d;
    }

    @Override
    public Health health() {
        Optional<OperatingSystemMXBean> osBeanOpt = getPlatformOsMxBean();
        if (osBeanOpt.isEmpty()) {
            return Health.unknown().withDetail("error", "OperatingSystemMXBean not available").build();
        }

        OperatingSystemMXBean osBean = osBeanOpt.get();
        double load = osBean.getProcessCpuLoad(); // value in range [0.0,1.0] or -1 if not available

        if (load < 0) {
            return Health.unknown().withDetail("error", "processCpuLoad not available").build();
        }

        double roundedLoad = round(load);
        String pct = PCT.format(roundedLoad);

        var details = Health.up().withDetail("processCpuLoad", roundedLoad)
                .withDetail("processCpuLoadPercent", pct)
                .withDetail("warnThreshold", warnThreshold)
                .withDetail("downThreshold", downThreshold);

        if (roundedLoad >= downThreshold) {
            return Health.down().withDetails(details.build().getDetails()).withDetail("error", "CPU load is critical").build();
        } else if (roundedLoad >= warnThreshold) {
            return Health.status("WARN").withDetails(details.build().getDetails()).withDetail("message", "CPU load is high").build();
        } else {
            return details.build();
        }
    }
}

