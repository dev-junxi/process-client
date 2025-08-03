package com.susu.processclient;


import org.springframework.core.Ordered;

public class TailProcessor implements BaseProcessor {

    @Override
    public ProcessResult call(ProcessClientParam request, ProcessorChain chain) {
        return request.getResult();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {

        public TailProcessor build() {
            return new TailProcessor();
        }
    }
}
