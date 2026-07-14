package ai.chat2db.plugin.mysql.completion.provider.collation;

import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidateBuildResult;
import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.plugin.mysql.completion.util.MysqlSqlCompletionTokenUtil;
import ai.chat2db.mysql.parser.base.MySqlLexer;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.antlr.v4.runtime.Token;
import org.apache.commons.lang3.StringUtils;


public final class MysqlSqlCompletionCollationCandidateProvider {

    private static final List<String> COLLATIONS = List.of(
            "ascii_bin",
            "ascii_general_ci",
            "big5_bin",
            "big5_chinese_ci",
            "binary",
            "cp1250_bin",
            "cp1250_general_ci",
            "cp1251_bin",
            "cp1251_general_ci",
            "cp1256_bin",
            "cp1256_general_ci",
            "cp1257_bin",
            "cp1257_general_ci",
            "cp850_bin",
            "cp850_general_ci",
            "cp852_bin",
            "cp852_general_ci",
            "cp866_bin",
            "cp866_general_ci",
            "cp932_bin",
            "cp932_japanese_ci",
            "dec8_bin",
            "dec8_swedish_ci",
            "eucjpms_bin",
            "eucjpms_japanese_ci",
            "euckr_bin",
            "euckr_korean_ci",
            "gb18030_bin",
            "gb18030_chinese_ci",
            "gb2312_bin",
            "gb2312_chinese_ci",
            "gbk_bin",
            "gbk_chinese_ci",
            "geostd8_bin",
            "geostd8_general_ci",
            "greek_bin",
            "greek_general_ci",
            "hebrew_bin",
            "hebrew_general_ci",
            "hp8_bin",
            "hp8_english_ci",
            "keybcs2_bin",
            "keybcs2_general_ci",
            "koi8r_bin",
            "koi8r_general_ci",
            "koi8u_bin",
            "koi8u_general_ci",
            "latin1_bin",
            "latin1_general_ci",
            "latin1_general_cs",
            "latin1_swedish_ci",
            "latin2_bin",
            "latin2_general_ci",
            "latin5_bin",
            "latin5_turkish_ci",
            "latin7_bin",
            "latin7_general_ci",
            "macce_bin",
            "macce_general_ci",
            "macroman_bin",
            "macroman_general_ci",
            "sjis_bin",
            "sjis_japanese_ci",
            "swe7_bin",
            "swe7_swedish_ci",
            "tis620_bin",
            "tis620_thai_ci",
            "ucs2_bin",
            "ucs2_general_ci",
            "ucs2_unicode_520_ci",
            "ucs2_unicode_ci",
            "ujis_bin",
            "ujis_japanese_ci",
            "utf16_bin",
            "utf16_general_ci",
            "utf16_unicode_520_ci",
            "utf16_unicode_ci",
            "utf16le_bin",
            "utf16le_general_ci",
            "utf32_bin",
            "utf32_general_ci",
            "utf32_unicode_520_ci",
            "utf32_unicode_ci",
            "utf8_bin",
            "utf8_general_ci",
            "utf8_unicode_520_ci",
            "utf8_unicode_ci",
            "utf8mb3_bin",
            "utf8mb3_general_ci",
            "utf8mb3_unicode_520_ci",
            "utf8mb3_unicode_ci",
            "utf8mb4_0900_ai_ci",
            "utf8mb4_0900_as_ci",
            "utf8mb4_0900_as_cs",
            "utf8mb4_0900_bin",
            "utf8mb4_bin",
            "utf8mb4_general_ci",
            "utf8mb4_unicode_520_ci",
            "utf8mb4_unicode_ci");

    private MysqlSqlCompletionCollationCandidateProvider() {
    }

    public static MysqlSqlCompletionCandidateBuildResult build(MysqlSqlCompletionCandidateContext context,
                                                               SqlCompletionCandidates c3Result) {
        String prefix = context == null ? "" : context.prefix();
        if (StringUtils.isBlank(prefix)) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        Optional<String> charsetScope = charsetScope(context);
        List<SqlCompletionCandidate> candidates = COLLATIONS.stream()
                .filter(collation -> matchesCharsetScope(collation, charsetScope))
                .filter(collation -> StringUtils.startsWithIgnoreCase(collation, prefix))
                .map(MysqlSqlCompletionCollationCandidateProvider::candidate)
                .toList();
        return MysqlSqlCompletionCandidateBuildResult.success(candidates);
    }

    private static Optional<String> charsetScope(MysqlSqlCompletionCandidateContext context) {
        if (context == null || context.window() == null || context.cursorContext() == null) {
            return Optional.empty();
        }
        String sqlBeforeReplacement = StringUtils.left(context.window().sourceSql(), context.cursorContext()
                .replaceStart());
        List<Token> tokens = MysqlSqlCompletionTokenUtil.tokens(sqlBeforeReplacement);
        String charset = null;
        for (int index = 0; index < tokens.size(); index++) {
            Token token = tokens.get(index);
            if (!MysqlSqlCompletionTokenUtil.isDefaultToken(token)) {
                continue;
            }
            if (token.getType() == MySqlLexer.CHARSET) {
                int valueIndex = charsetValueTokenIndex(tokens, index + 1);
                if (valueIndex >= 0) {
                    charset = normalizeIdentifier(tokens.get(valueIndex).getText());
                }
            } else if (token.getType() == MySqlLexer.CHARACTER) {
                int setIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, index + 1);
                if (setIndex >= 0 && tokens.get(setIndex).getType() == MySqlLexer.SET) {
                    int valueIndex = charsetValueTokenIndex(tokens, setIndex + 1);
                    if (valueIndex >= 0) {
                        charset = normalizeIdentifier(tokens.get(valueIndex).getText());
                    }
                }
            }
        }
        return StringUtils.isBlank(charset) ? Optional.empty() : Optional.of(charset);
    }

    private static int charsetValueTokenIndex(List<Token> tokens, int startIndex) {
        int valueIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, startIndex);
        if (valueIndex < 0) {
            return -1;
        }
        if (tokens.get(valueIndex).getType() == MySqlLexer.EQUAL_SYMBOL) {
            valueIndex = MysqlSqlCompletionTokenUtil.nextDefaultIndex(tokens, valueIndex + 1);
        }
        if (valueIndex < 0 || tokens.get(valueIndex).getType() == Token.EOF) {
            return -1;
        }
        return valueIndex;
    }

    private static String normalizeIdentifier(String text) {
        return MysqlSqlCompletionTokenUtil.stripQuote(StringUtils.defaultString(text))
                .toLowerCase(Locale.ROOT);
    }

    private static boolean matchesCharsetScope(String collation, Optional<String> charsetScope) {
        if (charsetScope.isEmpty()) {
            return true;
        }
        String charset = charsetScope.get();
        if ("binary".equalsIgnoreCase(charset)) {
            return "binary".equalsIgnoreCase(collation);
        }
        return StringUtils.startsWithIgnoreCase(collation, charset + "_");
    }

    private static SqlCompletionCandidate candidate(String collation) {
        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.KEYWORD,
                collation);
        candidate.setInsertText(collation);
        candidate.setSortRank(700);
        return candidate;
    }
}
