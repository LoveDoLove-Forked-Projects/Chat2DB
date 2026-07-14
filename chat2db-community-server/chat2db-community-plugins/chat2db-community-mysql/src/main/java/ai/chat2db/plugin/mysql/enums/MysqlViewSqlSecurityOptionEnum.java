package ai.chat2db.plugin.mysql.enums;

import ai.chat2db.community.tools.util.I18nUtils;
import ai.chat2db.community.domain.api.enums.plugin.InputTypeEnum;
import ai.chat2db.community.domain.api.model.form.FormConfig;
import ai.chat2db.community.domain.api.model.form.SelectOption;

import java.util.ArrayList;

public enum MysqlViewSqlSecurityOptionEnum {

    DEFINER, INVOKER;
    public static FormConfig getFormConfig() {
        FormConfig formConfig = new FormConfig();
        formConfig.setLabelName(I18nUtils.getMessage("gui.modify.view.config.sqlSecurity"));
        formConfig.setName("security");
        formConfig.setInputType(InputTypeEnum.SELECT.name().toLowerCase());
        MysqlViewSqlSecurityOptionEnum[] values = MysqlViewSqlSecurityOptionEnum.values();
        int length = values.length;
        ArrayList<SelectOption> selectOptions = new ArrayList<>(length+1);
        for (int i = 0; i < length; i++) {
            SelectOption selectOption = new SelectOption(values[i].name(),i);
            selectOptions.add(selectOption);
        }
        selectOptions.add(new SelectOption(null,length));
        formConfig.setSelects(selectOptions);
        formConfig.setDefaultValue(length+"");
        return formConfig;
    }
}
