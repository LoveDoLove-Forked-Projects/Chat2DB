package ai.chat2db.spi.util;

import ai.chat2db.community.domain.api.model.metadata.Database;
import ai.chat2db.community.domain.api.model.metadata.Schema;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortUtils {

    public static List<Database> sortDatabase(List<Database> databases, List<String> list, Connection connection) {
        if (CollectionUtils.isEmpty(databases)) {
            return databases;
        }
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        for (Database database : databases) {
            if (list.contains(database.getName())) {
                database.setSystem(true);
            }
        }

        String ulr;
        try {
            ulr = connection.getMetaData().getURL();
        } catch (SQLException e) {
            return databases;
        }
        int no = -1;
        for (int i = 0; i < databases.size(); i++) {
            if (StringUtils.isNotBlank(ulr)
                    && StringUtils.isNotBlank(databases.get(i).getName())
                    && ulr.contains(databases.get(i).getName())
                    && !"mysql".equalsIgnoreCase(databases.get(i).getName())) {
                no = i;
                break;
            }
        }
        if (no != -1 && no != 0) {
            Collections.swap(databases, no, 0);
        }
        return databases;
    }

    public static List<Schema> sortSchema(List<Schema> schemas, List<String> systemSchemas) {
        if (CollectionUtils.isEmpty(schemas)) {
            return schemas;
        }
        if (CollectionUtils.isEmpty(systemSchemas)) {
            systemSchemas = new ArrayList<>();
        }
        for (Schema schema : schemas) {
            if (systemSchemas.contains(schema.getName())) {
                schema.setSystem(true);
            }
        }
        return schemas;
    }
}
