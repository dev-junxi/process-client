package com.susu.processclient;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class ChainIdFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String chainId = ChainIdHolder.getChainId();
        if (chainId != null) {
            template.header("X-PROCESS-CHAIN-ID", chainId);
        }
        Integer chainIndex = ChainIndexHolder.getChainIndex();
        if (chainIndex != null) {
            template.header("X-PROCESS-CHAIN-INDEX", chainIndex.toString());
        }
    }
}
