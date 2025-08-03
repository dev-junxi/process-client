package com.susu.processclient;


//@Configuration
//@RefreshScope
//public class ProcessClientConfig {
//
//    @Value("${process.log.level}") // 假设在Nacos中配置了这个key
//    private String level;
//
//    @Bean
//    @RefreshScope
//    public ProcessClient processClient(){
//        ProcessClient processClient = ProcessClient.builder()
//                .defaultLogLevel(level)
//                .build();
//        ProcessClientHolder.set(processClient);
//        return processClient;
//    }
//}
