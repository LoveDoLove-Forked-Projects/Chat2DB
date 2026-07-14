package ai.chat2db.spi.parser.dialect;

import ai.chat2db.spi.ISQLDialect;

import ai.chat2db.spi.constant.SQLConstants;
import ai.chat2db.spi.IRuleManager;
import org.antlr.v4.runtime.Token;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractSQLDialect implements ISQLDialect {

    private static final Set<String> SQL_DELIMITERS = new HashSet<>();
    private static final Set<String> SQL_START_KEYWORDS = new HashSet<>();
    private static final Set<String> SQL_BLOCK_BEGIN = new HashSet<>();
    private static final Set<String> SQL_BLOCK_END = new HashSet<>();
    private static final Set<String> SQL_KEYWORDS = new HashSet<>();
    private static final Set<String> SQL_SET_DELIMITERS = new HashSet<>();
    private static final Set<String> SQL_BLOCK_TOGGLE_SYMBOL = new HashSet<>();
    private static final Set<String> SQL_INDEPENDENT_BLOCK_HEADERS = new HashSet<>();
    private static final Set<String> SQL_DEPENDENT_BLOCK_HEADERS = new HashSet<>();
    private static final Set<String> SQL_BLOCK_HEADER_PREFIXES = new HashSet<>();
    private static final Set<String> SQL_FUNCTION_NAMES = new HashSet<>();
    private static final Set<String> SQL_INNER_BLOCK_PREFIXES = new HashSet<>();
    private static final Set<String> SQL_PACKAGE_BODY_INNER_BLOCK_HEADERS = new HashSet<>();


    static {
        SQL_DELIMITERS.addAll(Set.of(SQLConstants.DEFAULT_STATEMENT_DELIMITER));
        SQL_START_KEYWORDS.addAll(Set.of(
                "SELECT", "INSERT", "UPDATE", "DELETE", "ALTER", "DROP", "COMMIT", "CREATE",
                "GRANT", "RENAME", "REVOKE", "COMMENT", "EXPLAIN", "ROLLBACK", "WITH", ";",
                "TRUNCATE", "REPLACE", "CALL", "LOAD", "DO", "HANDLER", "GET", "START", "BEGIN",
                "SAVEPOINT", "RELEASE", "LOCK", "UNLOCK", "PURGE", "KILL", "CHANGE", "RESET",
                "SHOW", "DESCRIBE", "OPTIMIZE", "REPAIR", "CHECK", "STOP", "INSTALL", "UNINSTALL",
                "FLUSH", "DESC", "USE", "HELP", "SIGNAL", "RESIGNAL", "BINLOG", "CACHE",
                "ANALYZE", "EXECUTE", "PREPARE", "DEALLOCATE"
        ));
        SQL_BLOCK_BEGIN.addAll(Set.of("BEGIN", "CASE", "IF", "LOOP", "WHILE"));
        SQL_BLOCK_END.addAll(Set.of("END"));
        SQL_KEYWORDS.addAll(Set.of("SELECT", "INSERT", "UPDATE", "DELETE", "CREATE", "ANALYZE","END"));
        SQL_DEPENDENT_BLOCK_HEADERS.addAll(Set.of("FUNCTION", "PROCEDURE", "TRIGGER"));
        SQL_BLOCK_HEADER_PREFIXES.addAll(Set.of("CREATE"));
    }

    public Set<String> getFunctionNames() {
        return SQL_FUNCTION_NAMES;
    }

    public Set<String> getStatementDelimiters() {
        return SQL_DELIMITERS;
    }

    public Set<String> getBlockBeginKeywords() {
        return SQL_BLOCK_BEGIN;
    }

    public Set<String> getBlockEndKeywords() {
        return SQL_BLOCK_END;
    }

    public Set<String> getKeywords() {
        return SQL_KEYWORDS;
    }

    public Set<String> getBlockToggleSymbols() {
        return SQL_BLOCK_TOGGLE_SYMBOL;
    }

    public Set<String> getIndependentBlockHeaders() {
        return SQL_INDEPENDENT_BLOCK_HEADERS;
    }


    public Set<String> getDependentBlockHeaders() {
        return SQL_DEPENDENT_BLOCK_HEADERS;
    }


    public Set<String> getBlockHeaderPrefixes() {
        return SQL_BLOCK_HEADER_PREFIXES;
    }

    public Set<String> getSetDelimiters() {
        return SQL_SET_DELIMITERS;
    }

    @Override
    public Set<String> getSqlStartKeywords() {
        return SQL_START_KEYWORDS;
    }

    @Override
    public Set<String> getPackageBodyInnerBlockHeaders() {
        return SQL_PACKAGE_BODY_INNER_BLOCK_HEADERS;
    }

    @Override
    public IRuleManager getRuleManager() {
        return null;
    }

    @Override
    public Set<String> getInnerBlockPrefix() {
        return SQL_INNER_BLOCK_PREFIXES;
    }

    public boolean isFunctionName(String text) {
        return matchesKeyword(text, getFunctionNames());

    }

    public boolean isSetDelimiter(String text) {
        return matchesKeyword(text, getSetDelimiters());
    }

    public boolean isBlockToggleSymbol(String text) {
        return matchesKeyword(text, getBlockToggleSymbols());
    }


    public boolean isSqlStartKeyword(Token token) {
        return Objects.nonNull(token) && matchesKeyword(token.getText(), getSqlStartKeywords());
    }


    public boolean isBlockBegin(String text) {
        return matchesKeyword(text, getBlockBeginKeywords());
    }


    public boolean isBlockEnd(String text) {
        return matchesKeyword(text, getBlockEndKeywords());
    }


    public boolean isStatementDelimiter(String text) {
        return matchesKeyword(text, getStatementDelimiters());
    }


    public boolean isKeyword(String text) {
        return matchesKeyword(text, getKeywords());
    }

    public boolean isBlockHeaderPrefix(String text) {
        return matchesKeyword(text, getBlockHeaderPrefixes());
    }

    public boolean isDependentBlockHeader(String text) {
        return matchesKeyword(text, getDependentBlockHeaders());
    }

    public boolean isIndependentBlockHeader(String text) {
        return matchesKeyword(text, getIndependentBlockHeaders());
    }

    @Override
    public boolean isInnerBlockPrefix(String text) {
        return matchesKeyword(text, getInnerBlockPrefix());
    }


    @Override
    public boolean isPackageBodyInnerBlockHeader(String text) {
        return matchesKeyword(text, getPackageBodyInnerBlockHeaders());
    }


    private boolean matchesKeyword(String text, Collection<String> keywords) {
        if (StringUtils.isBlank(text) || CollectionUtils.isEmpty(keywords)) {
            return false;
        }
        return keywords.stream().anyMatch(keyword -> keyword.equalsIgnoreCase(text));
    }
}
