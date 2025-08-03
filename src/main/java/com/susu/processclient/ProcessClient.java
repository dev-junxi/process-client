package com.susu.processclient;


import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface ProcessClient {


    static Builder builder() {
        return new DefaultProcessClientBuilder();
    }

    ProcessClientRequestSpec param(ProcessParam processParam);


    interface Builder {

        Builder defaultProcessor(Processor... processor);

        Builder defaultProcessor(Consumer<ProcessorSpec> processorSpecConsumer);

        Builder defaultProcessor(List<Processor> processors);

        Builder defaultLogLevel(String level);

        ProcessClient build();
    }

    interface ProcessorSpec {
        ProcessorSpec param(String k, Object v);

        ProcessorSpec params(Map<String, Object> map);

        ProcessorSpec processors(Processor... processors);

        ProcessorSpec processors(List<Processor> processor);
    }

    interface ProcessClientRequestSpec {

        ProcessClientRequestSpec processResult(ProcessResult processResult);

        ProcessClientRequestSpec processors(Consumer<ProcessorSpec> consumer);

        ProcessClientRequestSpec processors(Processor... processor);

        ProcessClientRequestSpec processors(List<Processor> processors);

        ProcessClientRequestSpec processors(LProcessor lProcessor);

        ProcessClientRequestSpec param(ProcessParam processParam);

        ProcessClientRequestSpec logLevel(String level);

        ProcessClientRequestSpec tailProcessor();

        CallResponseSpec flow();
    }

    interface CallResponseSpec {

        CallResponseSpec execute();

        <T> T entity(Class<T> tClass);
    }
}
