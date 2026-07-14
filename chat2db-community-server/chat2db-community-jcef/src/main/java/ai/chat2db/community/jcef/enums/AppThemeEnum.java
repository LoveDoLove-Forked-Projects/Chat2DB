package ai.chat2db.community.jcef.enums;

import ai.chat2db.community.jcef.utils.ThemeUtil;
import lombok.Getter;

import java.awt.Color;


public enum AppThemeEnum {


    LIGHT("Light", new Color(255, 255, 255), new Color(31, 35, 40)),


    DARK("Dark", new Color(13, 17, 23), new Color(230, 237, 243)),


    DARK_DIMMED("Dark Dimmed", new Color(34, 39, 46), new Color(201, 209, 217)),


    AUTO("Auto", null, null);


    @Getter
    private final String displayName;
    private final Color backgroundColor;


    @Getter
    private final Color textColor;


    AppThemeEnum(String displayName, Color backgroundColor, Color textColor) {
        this.displayName = displayName;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
    }


    public Color getBackgroundColor() {
        if (this == AppThemeEnum.AUTO) {
            return ThemeUtil.isSystemInDarkMode()
                    ? AppThemeEnum.DARK.getBackgroundColor()
                    : AppThemeEnum.LIGHT.getBackgroundColor();
        }
        return backgroundColor;
    }


    public String getBackgroundColorRgbString() {
        if (backgroundColor == null) {
            return null;
        }
        return String.format("rgb(%d, %d, %d)",
                backgroundColor.getRed(),
                backgroundColor.getGreen(),
                backgroundColor.getBlue());
    }


    public String getTextColorRgbString() {
        if (textColor == null) {
            return null;
        }
        return String.format("rgb(%d, %d, %d)",
                textColor.getRed(),
                textColor.getGreen(),
                textColor.getBlue());
    }


    public static AppThemeEnum fromString(String name) {
        if (name == null) {
            return null;
        }
        for (AppThemeEnum theme : AppThemeEnum.values()) {
            if (theme.name().equalsIgnoreCase(name)) {
                return theme;
            }
        }
        return null;
    }
    @Override
    public String toString() {
        return displayName;
    }
}
