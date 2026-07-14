package ai.chat2db.plugin.mysql.completion.provider.charset;

import ai.chat2db.plugin.mysql.completion.plan.MysqlSqlCompletionCandidateBuildResult;
import ai.chat2db.community.domain.api.model.completion.core.SqlCompletionCandidates;
import ai.chat2db.plugin.mysql.model.completion.context.MysqlSqlCompletionCandidateContext;
import ai.chat2db.community.domain.api.enums.completion.SqlCompletionCandidateTypeEnum;
import ai.chat2db.community.domain.api.model.completion.SqlCompletionCandidate;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


public final class MysqlSqlCompletionCharsetCandidateProvider {

    private static final List<String> CHARSETS = List.of(
            "ARMSCII8",
            "ASCII",
            "BIG5",
            "BINARY",
            "CP1250",
            "CP1251",
            "CP1256",
            "CP1257",
            "CP850",
            "CP852",
            "CP866",
            "CP932",
            "DEC8",
            "EUCJPMS",
            "EUCKR",
            "GB18030",
            "GB2312",
            "GBK",
            "GEOSTD8",
            "GREEK",
            "HEBREW",
            "HP8",
            "KEYBCS2",
            "KOI8R",
            "KOI8U",
            "LATIN1",
            "LATIN2",
            "LATIN5",
            "LATIN7",
            "MACCE",
            "MACROMAN",
            "SJIS",
            "SWE7",
            "TIS620",
            "UCS2",
            "UJIS",
            "UTF16",
            "UTF16LE",
            "UTF32",
            "UTF8",
            "UTF8MB3",
            "UTF8MB4");

    private MysqlSqlCompletionCharsetCandidateProvider() {
    }

    public static MysqlSqlCompletionCandidateBuildResult build(MysqlSqlCompletionCandidateContext context,
                                                               SqlCompletionCandidates c3Result) {
        String prefix = context == null ? "" : context.prefix();
        if (StringUtils.isBlank(prefix)) {
            return MysqlSqlCompletionCandidateBuildResult.empty();
        }
        List<SqlCompletionCandidate> candidates = CHARSETS.stream()
                .filter(charset -> StringUtils.startsWithIgnoreCase(charset, prefix))
                .map(MysqlSqlCompletionCharsetCandidateProvider::candidate)
                .toList();
        return MysqlSqlCompletionCandidateBuildResult.success(candidates);
    }

    private static SqlCompletionCandidate candidate(String charset) {
        SqlCompletionCandidate candidate = SqlCompletionCandidate.of(SqlCompletionCandidateTypeEnum.KEYWORD, charset);
        candidate.setInsertText(charset);
        candidate.setSortRank(700);
        return candidate;
    }
}
