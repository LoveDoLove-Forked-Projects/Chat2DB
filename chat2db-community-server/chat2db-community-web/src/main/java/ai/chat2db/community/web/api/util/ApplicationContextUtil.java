package ai.chat2db.community.web.api.util;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;


@Lazy(false)
@Component
public class ApplicationContextUtil implements ApplicationContextAware {


    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtil.applicationContext = applicationContext;
    }


    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }


    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext != null) {
            return applicationContext.getBean(clazz);
        } else {
            return null;
        }
    }


    public static <T> T getBeanOfType(Class<T> clazz) {
        Map<String, T> map = getApplicationContext().getBeansOfType(clazz);
        if (map.isEmpty()) {
            throw new RuntimeException("bean of type " + clazz + " not found or more than one");
        }
        if (map.size() == 1) {
            return map.values().iterator().next();
        } else {
            for (T t : map.values()) {
                Class c = AopProxyUtils.ultimateTargetClass(t);
                if (clazz.equals(c)) {
                    return t;
                }
            }
            return null;
        }
    }


    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }


    public static String getProperty(String key) {
        return getApplicationContext().getEnvironment().getProperty(key);
    }
}
