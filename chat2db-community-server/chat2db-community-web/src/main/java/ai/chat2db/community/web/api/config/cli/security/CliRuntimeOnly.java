package ai.chat2db.community.web.api.config.cli.security;

import ai.chat2db.community.web.api.util.CliRuntimeUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(name = CliRuntimeUtils.RUNTIME_MODE_PROPERTY, havingValue = "cli")
public @interface CliRuntimeOnly {
}
