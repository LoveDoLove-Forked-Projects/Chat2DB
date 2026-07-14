package ai.chat2db.community.domain.core.impl.db;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import ai.chat2db.community.domain.api.service.db.IDbMybatisGenerateService;
import ai.chat2db.spi.model.datasource.ConnectInfo;
import ai.chat2db.spi.sql.Chat2DBContext;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig.Builder;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.converts.TypeConverts;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DbMybatisGenerateServiceImpl implements IDbMybatisGenerateService {

    @Override
    public void generateClass(String tableName, String schemaName, String exportPath) throws SQLException {
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
        if (StringUtils.isNotBlank(schemaName)) {
            connectInfo.setSchemaName(schemaName);
        }
        DbType dbType = DbType.getDbType(connectInfo.getDbType());
        if (StringUtils.isBlank(exportPath)) {
            exportPath = System.getProperty("user.dir");
        }
        File file = new File(exportPath + File.separator + tableName);
        if (!file.exists()) {
            file.mkdirs();
        }
        exportPath = file.getPath();
        Map<OutputFile, String> pathInfo = new HashMap<>();
        pathInfo.put(OutputFile.service, null);
        pathInfo.put(OutputFile.serviceImpl, null);
        pathInfo.put(OutputFile.xml, exportPath);
        pathInfo.put(OutputFile.controller, null);
        Connection connection = Chat2DBContext.getConnection();
        DataSource customDataSource = getDataSource(connection);
        try {
            Builder builderSource = new Builder(customDataSource).schema(schemaName)
                    .typeConvert(TypeConverts.getTypeConvert(dbType));
            table2Class(Collections.singletonList(tableName), builderSource, exportPath, pathInfo);
        } catch (Exception e) {
            log.error("generateClass error", e);
            throw new RuntimeException(e);
        }
    }

    private DataSource getDataSource(Connection connection) {
        return new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                return connection;
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return connection;
            }

            @Override
            public PrintWriter getLogWriter() throws SQLException {
                return null;
            }

            @Override
            public void setLogWriter(PrintWriter out) throws SQLException {
            }

            @Override
            public void setLoginTimeout(int seconds) throws SQLException {
            }

            @Override
            public int getLoginTimeout() throws SQLException {
                return 0;
            }

            @Override
            public <T> T unwrap(Class<T> iface) throws SQLException {
                return null;
            }

            @Override
            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                return false;
            }

            @Override
            public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                return null;
            }
        };
    }

    private void table2Class(List<String> tableList, Builder builderSource, String outputDir,
            Map<OutputFile, String> pathInfo) {
        FastAutoGenerator
                .create(builderSource)
                .globalConfig(builder -> builder.author("chat2db")
                        .dateType(DateType.ONLY_DATE)
                        .outputDir(outputDir))
                .packageConfig(builder -> builder.parent("com.my")
                        .entity("entity")
                        .pathInfo(pathInfo)
                        .mapper("mapper")
                        .xml(outputDir))
                .strategyConfig(builder -> builder.addInclude(tableList)
                        .entityBuilder()
                        .formatFileName("%sDO")
                        .enableFileOverride()
                        .enableLombok()
                        .mapperBuilder()
                        .enableFileOverride())
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
