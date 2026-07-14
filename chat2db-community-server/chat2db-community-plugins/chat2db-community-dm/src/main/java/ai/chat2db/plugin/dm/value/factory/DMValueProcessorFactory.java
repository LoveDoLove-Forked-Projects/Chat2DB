package ai.chat2db.plugin.dm.value.factory;

import ai.chat2db.plugin.dm.enums.type.DMColumnTypeEnum;
import ai.chat2db.plugin.dm.value.sub.DMBitProcessor;
import ai.chat2db.spi.DefaultValueProcessor;

import java.util.Map;


public class DMValueProcessorFactory {

    private static final Map<String, DefaultValueProcessor> PROCESSOR_MAP;

    static {

        PROCESSOR_MAP = Map.ofEntries(
                Map.entry(DMColumnTypeEnum.BIT.name(), new DMBitProcessor())
        );
    }

    public static DefaultValueProcessor getValueProcessor(String type) {
        return PROCESSOR_MAP.get(type);
    }
}
