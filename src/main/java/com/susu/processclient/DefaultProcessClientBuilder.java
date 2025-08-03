package com.susu.processclient;


import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DefaultProcessClientBuilder implements ProcessClient.Builder {

    protected final DefaultProcessClient.DefaultProcessClientRequestSpec defaultRequest;

    public DefaultProcessClientBuilder() {

        this.defaultRequest = new DefaultProcessClient.DefaultProcessClientRequestSpec(List.of(), Map.of());
//        this.defaultRequest.processors(processorSpec -> processorSpec.param("processResult",processResult));
    }


    @Override
    public ProcessClient.Builder defaultProcessor(Processor... processor) {
        this.defaultRequest.processors(processor);
        return this;
    }

    @Override
    public ProcessClient.Builder defaultProcessor(Consumer<ProcessClient.ProcessorSpec> processorSpecConsumer) {
        this.defaultRequest.processors(processorSpecConsumer);
        return this;
    }

    @Override
    public ProcessClient.Builder defaultProcessor(List<Processor> processors) {
        this.defaultRequest.processors(processors);
        return this;
    }

    @Override
    public ProcessClient.Builder defaultLogLevel(String level) {
        this.defaultRequest.logLevel(level);
        return this;
    }

    @Override
    public ProcessClient build() {
        return new DefaultProcessClient(this.defaultRequest);
    }


}
