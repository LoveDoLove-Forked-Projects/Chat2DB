package ai.chat2db.community.domain.api.model.form;

import ai.chat2db.community.domain.api.enums.plugin.InputTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormConfig {

    private String labelName;
    private String name;
    private String inputType;
    private String defaultValue;
    private Boolean required = false;
    private Boolean multiple = false;
    private String display;
    private List<SelectOption> selects;

    public FormConfig(String labelName, String name, String inputType, String defaultValue) {
        this.labelName = labelName;
        this.name = name;
        this.inputType = inputType;
        this.defaultValue = defaultValue;
    }


    public static FormConfig getInputForm(String labelName, String name) {
        return new FormConfig(labelName, name, InputTypeEnum.INPUT.name().toLowerCase(), null);
    }

    public static FormConfig getInputForm(String labelName, String name,String display) {
        FormConfig formConfig = new FormConfig(labelName, name, InputTypeEnum.INPUT.name().toLowerCase(), null);
        formConfig.setDisplay(display);
        return formConfig;
    }


    public static FormConfig getCheckBox(String labelName, String name) {
        return new FormConfig(labelName, name, InputTypeEnum.CHECKBOX.name().toLowerCase(), "false");
    }

}
