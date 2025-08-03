package com.susu.processclient;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.UUID;

public class ChainIdFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String preChainId = httpServletRequest.getHeader("X-PROCESS-CHAIN-ID");
            String preChainIndex = httpServletRequest.getHeader("X-PROCESS-CHAIN-INDEX");
            if (StringUtils.isNotBlank(preChainId)) {
                ChainIdHolder.setChainId(preChainId);
            } else {
                ChainIdHolder.setChainId(UUID.randomUUID().toString());
            }

            if (StringUtils.isNotBlank(preChainIndex) && preChainIndex.matches("\\d+")) {
                ChainIndexHolder.setChainIndex(Integer.parseInt(preChainIndex));
            }
            chain.doFilter(request, response);
        } finally {
            if (response instanceof HttpServletResponse httpServletResponse) {
                String chainId = ChainIdHolder.getChainId();
                if (chainId != null) {
                    httpServletResponse.setHeader("X-PROCESS-CHAIN-ID", chainId);
                }
            }
            ChainIdHolder.clear();
            ChainIndexHolder.clear();
        }
    }
}
