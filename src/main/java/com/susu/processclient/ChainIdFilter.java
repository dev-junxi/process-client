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
            String chainId = StringUtils.isBlank(preChainId) ? UUID.randomUUID().toString() : preChainId;
            ChainIdHolder.setChainId(chainId);
            if (StringUtils.isNotBlank(preChainIndex) && preChainIndex.matches("\\d+")) {
                ChainIndexHolder.setChainIndex(Integer.parseInt(preChainIndex));
            }
            if (response instanceof HttpServletResponse httpServletResponse) {
                httpServletResponse.setHeader("X-PROCESS-CHAIN-ID", chainId);
            }
            chain.doFilter(request, response);
        } finally {
            ChainIdHolder.clear();
            ChainIndexHolder.clear();
        }
    }
}
