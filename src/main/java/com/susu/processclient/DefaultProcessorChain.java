package com.susu.processclient;


import org.springframework.core.OrderComparator;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class DefaultProcessorChain implements ProcessorChain {


    private final List<CallProcessor> originCallProcessors;

    private final Deque<BaseProcessor> callProcessors;

    private final Map<String, Object> processorContext;

    private String logLevel;

    private String chainId;

    private int index = 0;


    DefaultProcessorChain(Deque<BaseProcessor> callProcessors, String logLevel, String chainId, Map<String, Object> processorContext) {
        Assert.notNull(callProcessors, "callProcessors can not be null");
        this.originCallProcessors = List.copyOf(callProcessors);
        this.callProcessors = callProcessors;
        this.logLevel = logLevel;
        this.chainId = chainId;
        this.processorContext = processorContext;
        Integer chainIndex = ChainIndexHolder.getChainIndex();
        //用户自定义索引优先级更高
        Object chainIndexUserCustom = this.processorContext.get(ProcessContextKey.CHAIN_INDEX);
        if (chainIndexUserCustom != null) {
            if (chainIndexUserCustom instanceof Integer i) {
                chainIndex = i;
            }
        }
        if (chainIndex != null) {
            this.index = chainIndex;
        } else {
            this.index = 0;
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public ProcessResult nextCall(ProcessClientParam request) {
        Assert.notNull(request, "request can not be null");
        if (this.callProcessors.isEmpty()) {
            return request.getResult();
        }

        var processor = this.callProcessors.pop();

        return processor.callProcessor(request, this);
    }

    @Override
    public List<CallProcessor> getProcessors() {
        return this.originCallProcessors;
    }

    @Override
    public String getLogLevel() {
        return this.logLevel;
    }

    @Override
    public int getIndex() {
        return this.index++;
    }

    @Override
    public String getChainId() {
        return this.chainId;
    }


    public static class Builder {
        private final Deque<BaseProcessor> callProcessors;

        private final Map<String, Object> processorContext = new HashMap<>();

        private String logLevel;

        private String chainId;

        public Builder() {
            this.callProcessors = new ConcurrentLinkedDeque<>();
        }

        public Builder push(Processor processor) {
            Assert.notNull(processor, "processor can not be null");
            return this.pushAll(List.of(processor));
        }

        public Builder pushAll(List<? extends Processor> processors) {
            Assert.notNull(processors, "callProcessor can not be null");
            Assert.noNullElements(processors, "callProcessor can not contain null");
            if (!CollectionUtils.isEmpty(processors)) {
                List<BaseProcessor> baseProcessorList = processors.stream()
                        .filter(a -> a instanceof BaseProcessor)
                        .map(a -> (BaseProcessor) a)
                        .toList();

                if (!CollectionUtils.isEmpty(baseProcessorList)) {
                    baseProcessorList.forEach(this.callProcessors::push);
                }

                this.reOrder();
            }

            return this;


        }

        public Builder logLevel(String level) {
            this.logLevel = level;
            return this;
        }

        public Builder chainId(String chainId) {
            this.chainId = chainId;
            return this;
        }

        public Builder processorContext(String key, Object value) {
            this.processorContext.put(key, value);
            return this;
        }

        private void reOrder() {

            ArrayList<BaseProcessor> processors = new ArrayList<>(this.callProcessors);
            OrderComparator.sort(processors);
            this.callProcessors.clear();
            processors.forEach(this.callProcessors::push);
        }

        public DefaultProcessorChain build() {
            return new DefaultProcessorChain(this.callProcessors, this.logLevel, this.chainId, this.processorContext);
        }
    }
}
