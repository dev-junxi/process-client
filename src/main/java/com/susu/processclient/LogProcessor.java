package com.susu.processclient;


import cn.hutool.core.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface LogProcessor extends BaseProcessor {

    Logger logger = LoggerFactory.getLogger(LogProcessor.class);

    @Override
    default ProcessResult callProcessor(ProcessClientParam request, ProcessorChain chain) {

        List<String> levels = new ArrayList<>();
        levels.add(LogLevel.INFO);
        try {
            return chain.nextCall(request);
        } catch (Exception e) {
            levels.add(LogLevel.DEBUG);
            ProcessSnapshot exceptionSnapshot = buildExceptionSnapshot(request.getCurrentSnapshot(), e);
            request.getSnapshots().add(exceptionSnapshot);
            String formatted = "chainId:%s 发生异常".formatted(chain.getChainId());
            logger.error(formatted, e);
            throw e;
        } finally {
            if (levels.contains(chain.getLogLevel())) {
                call(request, chain);
            }
        }

    }


    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }


    default ProcessSnapshot buildExceptionSnapshot(ProcessSnapshot processSnapshot, Exception e) {
        return ProcessSnapshot.builder()
                .chainId(processSnapshot.getChainId())
                .stepIndex(processSnapshot.getStepIndex())
                .hostMethod(processSnapshot.getHostMethod())
                .stepName(processSnapshot.getStepName())
                .stepParam(processSnapshot.getStepParam())
                .stepResult(processSnapshot.getStepResult())
                .stepException(e.getClass().getSimpleName())
                .stepExceptionMessage(e.getMessage())
                .stepExceptionStackTrace(StringUtils.truncateTo64KConservative(getStackTrace(e)))
                .isSuccess("0")
                .stepStartTime(processSnapshot.getStepStartTime())
                .stepEndTime(DateUtil.formatLocalDateTime(LocalDateTime.now()))
                .build();
    }

    default String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
