package com.susu.processclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class ProcessSnapshotCleanupTask {
    private final DataSource dataSource;

    public ProcessSnapshotCleanupTask(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Scheduled(cron = "0 0 1 ? * SUN") // 每周日凌晨1点
    public void cleanupOldSnapshots() {
        Date threeMonthsAgo = Date.from(
                LocalDateTime.now()
                        .minusMonths(1)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM process_snapshot WHERE created_time < ?"
             )) {
            statement.setTimestamp(1, new java.sql.Timestamp(threeMonthsAgo.getTime()));
            int deletedRows = statement.executeUpdate();
            log.info("删除 {} 条历史数据 process snapshots", deletedRows);
        } catch (Exception e) {
            log.error("清除历史数据失败 process snapshots", e);
        }
    }
}




