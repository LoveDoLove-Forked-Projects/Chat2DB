package ai.chat2db.plugin.sqlserver.enums;

import ai.chat2db.community.tools.util.I18nUtils;
import ai.chat2db.community.domain.api.enums.plugin.InputTypeEnum;
import ai.chat2db.community.domain.api.model.form.FormConfig;
import ai.chat2db.community.domain.api.model.form.SelectOption;

import java.util.ArrayList;

public enum SqlServerViewCheckOptionEnum {

    CHECK_OPTION("CHECK OPTION");
    private final String description;

    SqlServerViewCheckOptionEnum (String description) {
        this.description = description;
    }
    public static FormConfig getFormConfig() {
        FormConfig formConfig = new FormConfig();
        formConfig.setLabelName(I18nUtils.getMessage("gui.modify.view.config.checkOption"));
        formConfig.setName("checkOption");
        formConfig.setInputType(InputTypeEnum.SELECT.name().toLowerCase());
        SqlServerViewCheckOptionEnum[] values = SqlServerViewCheckOptionEnum.values();
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
