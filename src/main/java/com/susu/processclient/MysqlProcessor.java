package com.susu.processclient;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
public class MysqlProcessor implements LogProcessor {


    @Override
    public ProcessResult call(ProcessClientParam request, ProcessorChain chain) {

        CompletableFuture.runAsync(() -> {

            try {
                List<ProcessSnapshot> snapshots = request.getSnapshots();
                if (CollectionUtils.isEmpty(snapshots)) {
                    return;
                }
                ApplicationContext context = SpringApplicationContextHolder.getApplicationContext();

                JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);

                // 定义SQL语句
                String sql = """
                        INSERT INTO process_snapshot (chain_id, host_method, step_index, step_name, step_param, 
                        step_result, step_exception, step_exception_message, step_exception_stack_trace, 
                        is_success, step_start_time, step_end_time) 
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """;

                List<Object[]> batchArgs = convertSqlparam(snapshots);

                jdbcTemplate.batchUpdate(sql, batchArgs);
            } catch (Exception e) {
                log.error("processClient日志打印异常", e);
                throw e;
            }

        });


        return request.getResult();
    }

    private List<Object[]> convertSqlparam(List<ProcessSnapshot> snapshots) {
        return snapshots.stream()
                .map(snapshot -> new Object[]{
                        snapshot.getChainId(),
                        snapshot.getHostMethod(),
                        snapshot.getStepIndex(),
                        snapshot.getStepName(),
                        snapshot.getStepParam(),
                        snapshot.getStepResult(),
                        snapshot.getStepException(),
                        snapshot.getStepExceptionMessage(),
                        snapshot.getStepExceptionStackTrace(),
                        snapshot.getIsSuccess(),
                        snapshot.getStepStartTime(),
                        snapshot.getStepEndTime()
                })
                .collect(Collectors.toList());
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {

        public MysqlProcessor build() {
            return new MysqlProcessor();
        }
    }
}
