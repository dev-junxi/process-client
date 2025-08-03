package com.susu.processclient;

import org.springframework.core.Ordered;

public interface Processor extends Ordered {


    int DEFAULT_PRECEDENCE_ORDER = Ordered.HIGHEST_PRECEDENCE + 1000;

    String getName();

}
