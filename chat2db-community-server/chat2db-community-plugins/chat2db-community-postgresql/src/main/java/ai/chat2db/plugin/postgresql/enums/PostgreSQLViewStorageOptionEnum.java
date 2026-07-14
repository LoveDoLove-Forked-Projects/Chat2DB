package ai.chat2db.plugin.postgresql.enums;

import ai.chat2db.community.tools.util.I18nUtils;
import ai.chat2db.community.domain.api.enums.plugin.InputTypeEnum;
import ai.chat2db.community.domain.api.model.form.FormConfig;
import ai.chat2db.community.domain.api.model.form.SelectOption;

import java.util.ArrayList;

public enum PostgreSQLViewStorageOptionEnum {


    TEMP("TEMP"),
    LOCAL_TEMP("LOCAL TEMP"),
    GLOBAL_TEMP("GLOBAL TEMP"),
    UNLOGGED(" UNLOGGED");
    private final String description;

    PostgreSQLViewStorageOptionEnum(String description) {
        this.description = description;
    }

    public static FormConfig getFormConfig() {
        FormConfig formConfig = new FormConfig();
        formConfig.setLabelName(I18nUtils.getMessage("gui.modify.view.config.storage"));
        formConfig.setName("storageClause");
        formConfig.setInputType(InputTypeEnum.SELECT.name().toLowerCase());
        PostgreSQLViewStorageOptionEnum[] values = PostgreSQLViewStorageOptionEnum.values();
        int length = values.length;
        ArrayList<SelectOption> selectOptions = new ArrayList<>(length + 1);
        for (int i = 0; i < length; i++) {
            SelectOption selectOption = new SelectOption(values[i].description, i);
            selectOptions.add(selectOption);
        }
        selectOptions.add(new SelectOption(null, length));
        formConfig.setSelects(selectOptions);
        formConfig.setDefaultValue(length + "");
        return formConfig;
    }
}
