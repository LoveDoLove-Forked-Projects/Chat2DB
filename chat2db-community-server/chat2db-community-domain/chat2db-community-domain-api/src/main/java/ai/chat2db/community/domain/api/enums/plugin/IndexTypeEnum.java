package ai.chat2db.community.domain.api.enums.plugin;


import ai.chat2db.community.domain.api.model.metadata.IndexType;
import lombok.Getter;
import lombok.Setter;


@Getter
public enum IndexTypeEnum {

    NORMAL("Normal", "INDEX"),

    UNIQUE("Unique", "UNIQUE INDEX"),
    ;

    private String name;

    private String keyword;

    @Setter
    private IndexType indexType;

    IndexTypeEnum(String name, String keyword) {
        this.name = name;
        this.keyword = keyword;
        this.indexType = new IndexType(name);
    }
}
