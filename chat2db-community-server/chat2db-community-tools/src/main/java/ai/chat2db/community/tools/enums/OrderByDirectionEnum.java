package ai.chat2db.community.tools.enums;


public enum OrderByDirectionEnum implements IBaseEnum<String> {


    ASC,


    DESC;

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getDescription() {
        return this.name();
    }
}
