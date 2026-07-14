package ai.chat2db.community.start.test.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import ai.chat2db.community.start.test.common.BaseTest;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;


@Slf4j
public class MybatisGeneratorTest extends BaseTest {
    @Resource
    private DataSource dataSource;

    @Test
    public void coreGenerator() {
        doGenerator(Lists.newArrayList("operation_log"));
    }

    private void doGenerator(List<String> tableList) {
        String outputDir = System.getProperty("user.dir")
            + "/../chat2db-community-domain/chat2db-community-domain-repository/src/main"
            + "/java";
        String xmlDir = System.getProperty("user.dir")
            + "/../chat2db-community-domain/chat2db-community-domain-repository/src/main"
            + "/resources/mapper";
        Map<OutputFile, String> pathInfo = new HashMap<>();
        pathInfo.put(OutputFile.service, null);
        pathInfo.put(OutputFile.serviceImpl, null);
        pathInfo.put(OutputFile.xml, xmlDir);
        pathInfo.put(OutputFile.controller, null);

        FastAutoGenerator
            .create(new DataSourceConfig.Builder(dataSource)
                .typeConvert(new MySqlTypeConvert()))
            .globalConfig(builder -> {
                builder.author("chat2db")
                    .disableOpenDir()
                    .dateType(DateType.ONLY_DATE)
                    .outputDir(outputDir);
            })
            .packageConfig(builder -> {
                builder.parent("ai.chat2db.community.domain.repository")
                    .entity("entity")
                    .pathInfo(pathInfo)
                    .mapper("mapper");
            })
            .strategyConfig(builder -> {
                builder.addInclude(tableList)
                    .entityBuilder()
                    .formatFileName("%sDO")
                    .enableFileOverride()
                    .enableLombok()
                    .mapperBuilder()
                    .enableFileOverride()
                ;

            })
            .templateEngine(new FreemarkerTemplateEngine())
            .execute();
    }

}
