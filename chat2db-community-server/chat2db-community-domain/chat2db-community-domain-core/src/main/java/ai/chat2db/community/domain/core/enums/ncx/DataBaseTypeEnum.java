package ai.chat2db.community.domain.core.enums.ncx;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;


@Getter
public enum DataBaseTypeEnum {


    MYSQL("jdbc:mysql://%s:%s"),


    ORACLE("jdbc:oracle:thin:@%s:%s:XE"),


    SQLSERVER("jdbc:sqlserver://%s:%s"),


    SQLITE("jdbc:sqlite:%s"),


    POSTGRESQL("jdbc:postgresql://%s:%s"),


    DB2("jdbc:db2://%s:%s"),


    MARIADB("jdbc:mariadb://%s:%s"),


    DM("jdbc:dm://%s:%s"),


    KINGBASE("jdbc:kingbase8://%s:%s"),


    PRESTO("jdbc:presto://%s:%s"),


    OCEANBASE("jdbc:oceanbase://%s:%s"),


    HIVE("jdbc:hive2://%s:%s"),


    CLICKHOUSE("jdbc:clickhouse://%s:%s"),


    MONGODB("jdbc:mongodb://%s:%s"),
    ;

    private String urlString;

    DataBaseTypeEnum(String urlString) {
        this.urlString = urlString;
    }

    public static DataBaseTypeEnum matchType(String value) {
        if (StringUtils.isNotEmpty(value)) {
            for (DataBaseTypeEnum dataBase : DataBaseTypeEnum.values()) {
                if (value.toUpperCase().contains(dataBase.name())) {
                    return dataBase;
                }
            }
        }
        return null;
    }

}
