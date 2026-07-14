package ai.chat2db.community.jcef.utils;

import ai.chat2db.community.tools.config.SystemSettingConstant;
import ai.chat2db.community.jcef.context.JcefContext;
import ai.chat2db.community.jcef.enums.AppThemeEnum;
import ai.chat2db.community.jcef.frame.MainJFrame;
import ai.chat2db.community.jcef.menus.Chat2DBMenuBar;
import ai.chat2db.community.jcef.menus.MenuI18n;
import lombok.extern.slf4j.Slf4j;
import org.cef.OS;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;


@Slf4j
public class SystemSettingsUtil {

    public static String getCachePath() {
        return ai.chat2db.community.tools.util.SystemSettingsUtil.getCachePath();
    }

    public static void setProperty(String key, Object newValue) {
        ai.chat2db.community.tools.util.SystemSettingsUtil.setProperty(key, newValue);
    }

    public static Object getProperty(String key) {
        return ai.chat2db.community.tools.util.SystemSettingsUtil.getProperty(key);
    }

    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        return ai.chat2db.community.tools.util.SystemSettingsUtil.getBooleanProperty(key, defaultValue);
    }

    public static boolean isMcpEnabled() {
        return ai.chat2db.community.tools.util.SystemSettingsUtil.isMcpEnabled();
    }

    public static String getOrCreateMcpAuthToken() {
        return ai.chat2db.community.tools.util.SystemSettingsUtil.getOrCreateMcpAuthToken();
    }

    public static String resetMcpAuthToken() {
        return ai.chat2db.community.tools.util.SystemSettingsUtil.resetMcpAuthToken();
    }

    public static void changeTheme(String theme) {
        AppThemeEnum appThemeEnum = AppThemeEnum.fromString(theme);
        if (OS.isMacintosh()) {
            MacThemeUtil.setThemeColor(appThemeEnum);
        }else {
            ThemeUtil.setThemeColor(appThemeEnum);
        }
        setProperty(SystemSettingConstant.SYSTEM_APPEARANCE, theme);
    }

    public static void changeLanguage(String language) {
        String[] split = language.split("-");
        MenuI18n.setLocale(new Locale(split[0], split[1]));
        Chat2DBMenuBar.refreshMenuBar();
        setProperty(SystemSettingConstant.SYSTEM_LANGUAGE, language);
    }

    public static void saveWindowsInfo() {
        MainJFrame mainJFrame = JcefContext.getInstance().getFrame_();
        saveWindowsWithAndHeight(mainJFrame);
        SystemSettingsUtil.setProperty(SystemSettingConstant.IS_MAX_WINDOW, mainJFrame.getExtendedState() ==  JFrame.MAXIMIZED_BOTH);
    }

    public static void saveWindowsWithAndHeight(Frame mainJFrame) {
        Dimension dimension = mainJFrame.getSize();
        double currentWidth = dimension.getWidth();
        double currentHeight = dimension.getHeight();
        SystemSettingsUtil.setProperty(SystemSettingConstant.WINDOW_WITH, currentWidth);
        SystemSettingsUtil.setProperty(SystemSettingConstant.WINDOW_HEIGHT, currentHeight);
    }


    public static void maximizeWindowAndSaveWindowInfo(Frame mainJFrame) {
        SystemSettingsUtil.saveWindowsWithAndHeight(mainJFrame);
        saveWindowsLocation(mainJFrame);
        mainJFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private static void saveWindowsLocation(Frame mainJFrame) {
        Rectangle bounds = mainJFrame.getBounds();
        double boundsX = bounds.getX();
        double boundsY = bounds.getY();
        SystemSettingsUtil.setProperty(SystemSettingConstant.BOUNDS_X, boundsX);
        SystemSettingsUtil.setProperty(SystemSettingConstant.BOUNDS_Y, boundsY);
    }


    public static void recoverWindowFromMaxState(Frame mainJFrame) {
        Double boundsX = (Double) SystemSettingsUtil.getProperty(SystemSettingConstant.BOUNDS_X);
        Double boundsY = (Double) SystemSettingsUtil.getProperty(SystemSettingConstant.BOUNDS_Y);
        Double width = (Double) SystemSettingsUtil.getProperty(SystemSettingConstant.WINDOW_WITH);
        Double height = (Double) SystemSettingsUtil.getProperty(SystemSettingConstant.WINDOW_HEIGHT);
        if (width != null && height != null) {
            mainJFrame.setExtendedState(Frame.NORMAL);
            mainJFrame.setSize(new java.awt.Dimension(width.intValue(), height.intValue()));
            if (boundsX != null && boundsY != null) {
                mainJFrame.setLocation(boundsX.intValue(), boundsY.intValue());
            } else {
                mainJFrame.setLocationRelativeTo(null);
            }
        }
    }
}
