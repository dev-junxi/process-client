package com.susu.processclient;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.time.LocalDateTime;


public interface BaseProcessor extends CallProcessor {
    Logger logger = LoggerFactory.getLogger(BaseProcessor.class);

    default ProcessResult callProcessor(ProcessClientParam request, ProcessorChain chain) {

        ProcessClientParam processorRequest = before(request, chain);
        ProcessResult processResult = call(processorRequest, chain);
        after(processResult, request, chain);
        return chain.nextCall(request);
    }


    default ProcessClientParam before(ProcessClientParam processClientParam, ProcessorChain chain) {
//        logger.info("processor:{}=====================request:{}", getName(), JSON.toJSONString(processClientParam));
        int index = chain.getIndex();
        ChainIndexHolder.setChainIndex(index);
        ProcessSnapshot snapshot = ProcessSnapshot.builder()
                .chainId(String.valueOf(chain.getChainId()))
                .hostMethod(processClientParam.context().getOrDefault(ProcessContextKey.HOST_METHOD, "").toString())
                .stepIndex(String.valueOf(index))
                .stepName(getName())
                .stepParam(StringUtils.truncateTo64KConservative(JSON.toJSONString(processClientParam.processParam())))
                .stepStartTime(DateUtil.formatLocalDateTime(LocalDateTime.now()))
                .build();
        processClientParam.setCurrentSnapshot(snapshot);

        return processClientParam;
    }

    default ProcessResult after(ProcessResult processResult, ProcessClientParam request, ProcessorChain chain) {
//        logger.info("processor:{}======================response:{}", getName(), JSON.toJSONString(processResult));
        ProcessSnapshot currentSnapshot = request.getCurrentSnapshot();
        ProcessSnapshot snapshot = ProcessSnapshot.builder()
                .chainId(currentSnapshot.getChainId())
                .stepIndex(currentSnapshot.getStepIndex())
                .hostMethod(currentSnapshot.getHostMethod())
                .stepName(currentSnapshot.getStepName())
                .stepParam(currentSnapshot.getStepParam())
                .stepResult(StringUtils.truncateTo64KConservative(JSON.toJSONString(processResult)))
                .isSuccess("1")
                .stepStartTime(currentSnapshot.getStepStartTime())
                .stepEndTime(DateUtil.formatLocalDateTime(LocalDateTime.now()))
                .build();
        request.getSnapshots().add(snapshot);
        return processResult;
    }

    @Override
    default String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }


}
