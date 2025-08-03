CREATE TABLE IF NOT EXISTS process_snapshot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    chain_id VARCHAR(64) NOT NULL COMMENT '流程链ID',
    host_method varchar(256) DEFAULT NULL COMMENT '宿主方法',
    step_index VARCHAR(32) NOT NULL COMMENT '步骤索引',
    step_name VARCHAR(128) NOT NULL COMMENT '步骤名称',
    step_param TEXT COMMENT '步骤参数(JSON格式)',
    step_result TEXT COMMENT '步骤执行结果(JSON格式)',
    step_exception VARCHAR(256) COMMENT '异常类型',
    step_exception_message TEXT COMMENT '异常消息',
    step_exception_stack_trace TEXT COMMENT '异常堆栈',
    is_success varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '是否成功(0:失败,1:成功)',
    step_start_time DATETIME NOT NULL COMMENT '步骤开始时间',
    step_end_time DATETIME COMMENT '步骤结束时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录修改时间',

    INDEX idx_chain_id (chain_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程步骤快照表';