package com.susu.processclient;


import java.util.List;

public interface ProcessorChain {

    ProcessResult nextCall(ProcessClientParam request);

    List<CallProcessor> getProcessors();

    String getLogLevel();

    int getIndex();

    String getChainId();
}
