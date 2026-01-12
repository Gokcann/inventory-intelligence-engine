package com.iie.core.shared.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
class PartitionManager {

    private final JdbcTemplate jdbc;

    PartitionManager(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ensureCurrentMonthPartition() {
        LocalDate now = LocalDate.now();
        createMonthlyPartition(now);
        createMonthlyPartition(now.plusMonths(1)); // next month
    }

    private void createMonthlyPartition(LocalDate date) {
        String partitionName = "audit_logs_" + date.format(DateTimeFormatter.ofPattern("yyyy_MM"));
        LocalDate start = date.withDayOfMonth(1);
        LocalDate end = start.plusMonths(1);

        String sql = """
            CREATE TABLE IF NOT EXISTS %s 
            PARTITION OF audit_logs 
            FOR VALUES FROM ('%s') TO ('%s')
            """.formatted(partitionName, start, end);

        try {
            jdbc.execute(sql);
        } catch (Exception e) {
            // partition may already exist
        }
    }
}
