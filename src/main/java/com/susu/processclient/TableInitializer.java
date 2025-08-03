package com.susu.processclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class TableInitializer implements InitializingBean {

    private DataSource dataSource;

    public TableInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void executeSchemaScript() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // 读取SQL文件
            String sql = loadSchemaScript();

            // 执行SQL
            statement.execute(sql);
            log.info("process_snapshot表初始化成功");
        } catch (SQLException | IOException e) {
            log.info("process_snapshot表初始化失败");
        }
    }

    private String loadSchemaScript() throws IOException {
        ClassPathResource resource = new ClassPathResource("schema/process_snapshot.sql");
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        executeSchemaScript();
    }
}
