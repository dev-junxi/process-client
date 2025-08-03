package com.susu.processclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DefaultProcessClient implements ProcessClient {

    private final DefaultProcessClientRequestSpec defaultProcessClientRequestSpec;

    public DefaultProcessClient(DefaultProcessClientRequestSpec defaultProcessClientRequestSpec) {
        Assert.notNull(defaultProcessClientRequestSpec, "defaultProcessClientRequestSpec can not be null");
        this.defaultProcessClientRequestSpec = defaultProcessClientRequestSpec;
    }

    @Override
    public ProcessClientRequestSpec param(ProcessParam processParam) {
        Assert.notNull(processParam, "request can not be null");
        DefaultProcessClientRequestSpec spec = new DefaultProcessClientRequestSpec(this.defaultProcessClientRequestSpec);
        spec.param(processParam);
        return spec;
    }


    public static class DefaultProcessClientRequestSpec implements ProcessClientRequestSpec {

        private final List<Processor> processors = new ArrayList<>();

        private final Map<String, Object> processorContext = new HashMap<>();

        private ProcessParam processParam;

        private String logLevel;

        DefaultProcessClientRequestSpec(DefaultProcessClientRequestSpec defaultProcessClientRequestSpec) {
            this(defaultProcessClientRequestSpec.processors, defaultProcessClientRequestSpec.processorContext, defaultProcessClientRequestSpec.processParam, defaultProcessClientRequestSpec.logLevel);
        }

        DefaultProcessClientRequestSpec(List<Processor> processors, Map<String, Object> processorParams, ProcessParam processParam, String logLevel) {
            Assert.notNull(processors, "processors can not be null");
            Assert.notNull(processorParams, "processorParams can not be null");
            Assert.notNull(logLevel, "logLevel can not be null");
            this.processors.addAll(processors);
            this.processorContext.putAll(processorParams);
            this.processParam = processParam;
            this.logLevel = logLevel;
        }

        DefaultProcessClientRequestSpec(List<Processor> processors, Map<String, Object> processorParams) {
            Assert.notNull(processors, "processors can not be null");
            Assert.notNull(processorParams, "processorParams can not be null");
            this.processors.addAll(processors);
            this.processorContext.putAll(processorParams);
        }

        @Override
        public ProcessClientRequestSpec processResult(ProcessResult processResult) {
            Assert.notNull(processResult, "processResult can not be null");
            this.processorContext.put("processResult", processResult);
            return this;
        }

        @Override
        public ProcessClientRequestSpec processors(Consumer<ProcessorSpec> consumer) {
            Assert.notNull(consumer, "consumer can not be null");
            var defaultProcessorSpec = new DefaultProcessorSpec();
            consumer.accept(defaultProcessorSpec);
            this.processorContext.putAll(defaultProcessorSpec.getParams());
            this.processors.addAll(defaultProcessorSpec.getProcessors());
            return this;
        }

        @Override
        public ProcessClientRequestSpec processors(Processor... processor) {
            Assert.notNull(processor, "processor can not be null");
            Assert.noNullElements(processor, "processor can not contain null");
            this.processors.addAll(List.of(processor));
            return this;
        }

        @Override
        public ProcessClientRequestSpec processors(List<Processor> processors) {
            Assert.notNull(processors, "processors can not be null");
            Assert.noNullElements(processors, "processors can not contain null");
            this.processors.addAll(processors);
            return this;
        }

        @Override
        public ProcessClientRequestSpec processors(LProcessor lProcessor) {
            Assert.notNull(lProcessor, "lProcessor can not be null");
            this.processors.add(lProcessor);
            return this;
        }

        @Override
        public ProcessClientRequestSpec param(ProcessParam processParam) {
            Assert.notNull(processParam, "request can not be null");
            this.processParam = processParam;
            return this;
        }

        @Override
        public ProcessClientRequestSpec logLevel(String level) {
            Assert.hasText(level, "logLevel can not be null");
            this.logLevel = level;
            return this;
        }

        @Override
        public ProcessClientRequestSpec tailProcessor() {
            TailProcessor tailProcessor = new TailProcessor();
            this.processors.add(tailProcessor);
            return this;
        }

        @Override
        public CallResponseSpec flow() {
            ProcessorChain processorChain = buildProcessorChain();
            ProcessClientParam processClientParam = ProcessClientParam.builder()
                    .request(this.processParam)
                    .context(this.processorContext)
                    .build();
            return new DefaultCallResponseSpec(processClientParam, processorChain);
        }

        private ProcessorChain buildProcessorChain() {
//            this.processors.add(TailProcessor.Builder().build());
            String chainId = ChainIdHolder.getChainId();
            //用户自定义链路ID优先级更高
            Object chainIdUserCustom = this.processorContext.get(ProcessContextKey.CHAIN_ID);
            if (chainIdUserCustom != null) {
                if (chainIdUserCustom instanceof String id) {
                    chainId = id;
                }
            }
            return DefaultProcessorChain.builder()
                    .pushAll(this.processors)
                    .logLevel(this.logLevel)
                    .processorContext(this.processorContext)
                    .chainId(chainId == null ? String.valueOf(System.currentTimeMillis()) : chainId)
                    .build();

        }
    }

    public static class DefaultCallResponseSpec implements CallResponseSpec {

        private final ProcessClientParam processClientParam;

        private final ProcessorChain chain;

        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        private ProcessResult processResult;


        public DefaultCallResponseSpec(ProcessClientParam processClientParam, ProcessorChain chain) {
            Assert.notNull(processClientParam, "processClientParam can not be null");
            Assert.notNull(chain, "chain can not be null");
            this.processClientParam = processClientParam;
            this.chain = chain;
        }

        @Override
        public CallResponseSpec execute() {
            ProcessResult r = doProcessResponse(this.processClientParam);
            this.processResult = r;
            return this;
        }

        @Override
        public <T> T entity(Class<T> tClass) {
            return OBJECT_MAPPER.convertValue(this.processResult, tClass);
        }

        private ProcessResult doProcessResponse(ProcessClientParam request) {
            return this.chain.nextCall(request);

        }
    }

    public static class DefaultProcessorSpec implements ProcessorSpec {

        private final List<Processor> processors = new ArrayList<>();

        private final Map<String, Object> params = new HashMap<>();

        @Override
        public ProcessorSpec param(String k, Object v) {
            Assert.hasText(k, "k can not be null or empty");
            Assert.notNull(v, "v can not be null");
            this.params.put(k, v);
            return this;
        }

        @Override
        public ProcessorSpec params(Map<String, Object> map) {
            Assert.notNull(map, "map can not be null");
            Assert.noNullElements(params.keySet(), "map can not contain null key");
            Assert.noNullElements(params.values(), "map can not contain null value");
            this.params.putAll(map);
            return this;
        }

        @Override
        public ProcessorSpec processors(Processor... processors) {
            Assert.notNull(processors, "processors can not be null");
            Assert.noNullElements(processors, "processors can not contain null");
            this.processors.addAll(List.of(processors));
            return this;
        }

        @Override
        public ProcessorSpec processors(List<Processor> processor) {
            Assert.notNull(processor, "processor can not be null");
            Assert.noNullElements(processor, "processor can not contain null");
            this.processors.addAll(processor);
            return this;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public List<Processor> getProcessors() {
            return processors;
        }
    }
}
