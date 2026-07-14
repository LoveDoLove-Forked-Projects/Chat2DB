package ai.chat2db.plugin.sqlite.enums.type;

import ai.chat2db.community.domain.api.model.metadata.Collation;

import java.util.Arrays;
import java.util.List;

public enum SqliteCollationEnum {

    BINARY("BINARY"),

    NOCASE("NOCASE"),

    RTRIM("RTRIM"),
    ;
    private Collation collation;

    SqliteCollationEnum(String collationName) {
        this.collation = new Collation(collationName);
    }

    public Collation getCollation() {
        return collation;
    }


    public static List<Collation> getCollations() {
        return Arrays.asList(SqliteCollationEnum.values()).stream().map(SqliteCollationEnum::getCollation).collect(java.util.stream.Collectors.toList());
    }

}
