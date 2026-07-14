package ai.chat2db.community.domain.core.impl.ai;

import ai.chat2db.community.domain.api.service.ai.IAiCharacterService;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiCharacterServiceImpl implements IAiCharacterService {

    private static final Pattern SQL_FENCE_PATTERN = Pattern.compile("```\\s*sql\\s*([\\s\\S]*?)```",
            Pattern.CASE_INSENSITIVE);

    @Override
    public String handle(String text) {
        Matcher matcher = SQL_FENCE_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim().replaceAll("[\u0000\u001A]", " ");
        }
        return text;
    }
}
