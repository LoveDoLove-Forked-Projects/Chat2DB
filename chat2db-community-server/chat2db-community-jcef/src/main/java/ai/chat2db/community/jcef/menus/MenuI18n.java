package ai.chat2db.community.jcef.menus;

import ai.chat2db.community.tools.config.SystemSettingConstant;
import ai.chat2db.community.tools.util.SystemSettingsUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MenuI18n {
    private static final String BUNDLE_NAME = "i18n.messages";
    private static ResourceBundle bundle;

    static {
        String language = (String) SystemSettingsUtil.getProperty(SystemSettingConstant.SYSTEM_LANGUAGE);
        if (StringUtils.isNotBlank(language)) {
            setLocale(Locale.forLanguageTag(language.replace('_', '-')));
        }else {
            setLocale(Locale.getDefault());
        }
    }


    public static void setLocale(Locale locale) {
        try {
            bundle = loadBundle(locale);
            System.out.println("Menu language switched to: " + locale.toLanguageTag());
        } catch (MissingResourceException e) {
            System.err.println("Unable to load resource bundle: " + locale.toLanguageTag() + ", falling back to the default locale.");
            bundle = loadFallbackBundle();
        }
    }

    private static ResourceBundle loadBundle(Locale locale) {
        return ResourceBundle.getBundle(BUNDLE_NAME, locale, MenuI18n.class.getClassLoader());
    }

    private static ResourceBundle loadFallbackBundle() {
        try {
            return loadBundle(Locale.ROOT);
        } catch (MissingResourceException ignored) {
            return new ResourceBundle() {
                @Override
                protected Object handleGetObject(String key) {
                    return key;
                }

                @Override
                public Enumeration<String> getKeys() {
                    return Collections.emptyEnumeration();
                }
            };
        }
    }

    public static String getString(String key) {
        if (bundle == null) {
            return key;
        }
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            System.err.println("Key not found in resource bundle: " + key);
            return key;
        }
    }
}
