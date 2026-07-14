package ai.chat2db.community.domain.api.enums.plugin;

public enum ObjectTypeEnum {

    TABLE,VIEW;

    public static boolean contains(String type) {
        return from(type) != null;
    }

    public static ObjectTypeEnum from(String type) {
        for (ObjectTypeEnum objectType : ObjectTypeEnum.values()) {
            if (objectType.name().equalsIgnoreCase(type)) {
                return objectType;
            }
        }
        return null;
    }
}
