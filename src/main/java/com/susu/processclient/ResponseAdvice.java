package com.susu.processclient;

//public class ResponseAdvice implements ResponseBodyAdvice<Object> {
//    @Override
//    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
//        return true;
//    }
//
//    @Override
//    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
//        String chainId = ChainIdHolder.getChainId();
//        if (chainId != null){
//            response.getHeaders().add("X-PROCESS-CHAIN-ID", chainId);
//        }
//        return body;
//    }
//}
