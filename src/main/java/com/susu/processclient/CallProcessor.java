package com.susu.processclient;


public interface CallProcessor extends Processor {

    ProcessResult call(ProcessClientParam request, ProcessorChain chain);

}
