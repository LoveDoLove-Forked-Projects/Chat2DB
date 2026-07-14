package ai.chat2db.community.start.test.dto;

import java.io.Serial;
import java.io.Serializable;

import ai.chat2db.community.tools.constant.IEasyToolsConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = IEasyToolsConstant.SERIAL_VERSION_UID;

    private String name;
}
