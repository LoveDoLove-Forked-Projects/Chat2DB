package ai.chat2db.community.tools.enums;

import lombok.Getter;


@Getter
public enum DataSourceTypeEnum implements IBaseEnum<String> {


    MYSQL("MySQL database connection"),


    REDIS("Redis database connection"),


    MONGODB("MongoDB database connection"),

    ;

    final String description;

    DataSourceTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
