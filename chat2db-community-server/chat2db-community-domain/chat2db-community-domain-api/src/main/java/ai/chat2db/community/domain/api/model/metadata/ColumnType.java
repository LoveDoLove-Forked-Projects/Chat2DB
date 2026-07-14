package ai.chat2db.community.domain.api.model.metadata;

import lombok.Data;

@Data
public class ColumnType {
    private String typeName;
    private boolean supportLength;
    private boolean supportScale;
    private boolean supportNullable;
    private boolean supportAutoIncrement;
    private boolean supportCharset;
    private boolean supportCollation;
    private boolean supportComments;
    private boolean supportDefaultValue;
    private boolean supportExtent;
    private boolean supportValue;
    private boolean supportUnit;
    private boolean supportOnUpdateCurrentTimestamp;
    public ColumnType(){

    }

    public ColumnType(String typeName, boolean supportLength, boolean supportScale, boolean supportNullable, boolean supportAutoIncrement, boolean supportCharset, boolean supportCollation, boolean supportComments, boolean supportDefaultValue, boolean supportExtent, boolean supportValue, boolean supportUnit) {
        this.typeName = typeName;
        this.supportLength = supportLength;
        this.supportScale = supportScale;
        this.supportNullable = supportNullable;
        this.supportAutoIncrement = supportAutoIncrement;
        this.supportCharset = supportCharset;
        this.supportCollation = supportCollation;
        this.supportComments = supportComments;
        this.supportDefaultValue = supportDefaultValue;
        this.supportExtent = supportExtent;
        this.supportValue = supportValue;
        this.supportUnit = supportUnit;
    }

    public ColumnType(String typeName, boolean supportLength, boolean supportScale, boolean supportNullable, boolean supportAutoIncrement, boolean supportCharset, boolean supportCollation, boolean supportComments, boolean supportDefaultValue, boolean supportExtent, boolean supportValue, boolean supportUnit, boolean supportOnUpdateCurrentTimestamp) {
        this.typeName = typeName;
        this.supportLength = supportLength;
        this.supportScale = supportScale;
        this.supportNullable = supportNullable;
        this.supportAutoIncrement = supportAutoIncrement;
        this.supportCharset = supportCharset;
        this.supportCollation = supportCollation;
        this.supportComments = supportComments;
        this.supportDefaultValue = supportDefaultValue;
        this.supportExtent = supportExtent;
        this.supportValue = supportValue;
        this.supportUnit = supportUnit;
        this.supportOnUpdateCurrentTimestamp = supportOnUpdateCurrentTimestamp;
    }
}
