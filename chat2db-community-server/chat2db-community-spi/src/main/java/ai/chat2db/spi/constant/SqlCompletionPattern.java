package ai.chat2db.spi.constant;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


public class SqlCompletionPattern {


    public static final Pattern STARTS_WITH_SPACE_NEWLINE_SEMICOLON_OR_RIGHT_PARENTHESIS = Pattern.compile("^[ \\r\\n,;)]");


    public static final Pattern LAST_WORD_JOIN_PATTERN = Pattern.compile("(?i)\\bjoin\\b$");


    public static final Pattern LAST_WORD_WHERE_OR_AND_PATTERN = Pattern.compile("(?i)\\b(where|and|or|group by|order by|having|on)\\b$");


    public static final Pattern FIRST_WORD_SELECT_PATTERN = Pattern.compile("(?i)^\\bselect\\b");


    public static final Pattern FIRST_WORD_FROM_PATTERN = Pattern.compile("(?i)^\\bfrom\\b");


    public static final Pattern KEYWORD_BEFORE_PERIOD_PATTERN = Pattern.compile("(?i)[\\s,](?:where|and|set|or|group by|order by|having|on)?[\\s,][^\\s,]*?\\.$");

    public static final Pattern END_PERIOD_PATTERN = Pattern.compile("([`\"\\[]?[a-zA-Z0-9_\\-]+[`\"\\]]?(?:\\.[`\"\\[]?[a-zA-Z0-9_\\-]+[`\"\\]]?){0,2})\\.$");
    public static final Pattern SELECT_STAR_FROM_PATTERN = Pattern.compile("(?i)^SELECT\\s+\\*\\s+FROM\\s*$");
    public static final Pattern SELECT_COUNT_FROM_PATTERN = Pattern.compile("(?i)^SELECT\\s+count\\(\\*\\)\\s+FROM\\s*$");
    public static final Pattern SELECT_FROM_PATTERN = Pattern.compile("(?i)^SELECT\\s+FROM\\s*$");


    public static final Pattern SELECT_TIP_COLUMN_FROM_PATTERN = Pattern.compile("(?i)^select\\s+(([^*]*,)?\\s*)from\\s+\\w+");
    public static final Pattern DELETE_FROM_PATTERN = Pattern.compile("(?i)^DELETE\\s+FROM\\s*$");
    public static final Pattern INSERT_INTO_PATTERN = Pattern.compile("(?i)^INSERT\\s+INTO\\s*$");
    public static final Pattern INSERT_IGNORE_INTO_PATTERN = Pattern.compile("(?i)^INSERT\\s+IGNORE\\s+INTO\\s*$");
    public static final Pattern REPLACE_INTO_PATTERN = Pattern.compile("(?i)^REPLACE\\s+INTO\\s*$");

    public static final Pattern UPDATE_TABLE_PATTERN = Pattern.compile("(?i)^UPDATE\\s*$");


    public static final Pattern SELECT_TABLE_NAME_PATTERN = Pattern.compile(
            "(?i)\\bfrom\\s+(([`\"\\[]?\\w+[`\"\\]]?\\.?)+)\\s*(?:AS\\s+)?(?!SET|WHERE|ON|GROUP|HAVING|ORDER|LEFT|RIGHT|INNER|OUTER|JOIN|LIMIT)(\\w+)?" +
                    "|\\bjoin\\s+(([`\"\\[]?\\w+[`\"\\]]?\\.?)+)\\s*(?:AS\\s+)?(?!SET|WHERE|ON|GROUP|HAVING|ORDER|LEFT|RIGHT|INNER|OUTER|JOIN|LIMIT)(\\w+)?"
    );


    public static final Pattern INSERT_TABLE_NAME_PATTERN = Pattern.compile(
            "(?i)\\binsert\\s+into\\s+((\\w+\\.?){1,3})(?:\\s+as\\s+)?\\s*(?!SET|WHERE|ON|GROUP|HAVING|ORDER|LEFT|RIGHT|INNER|OUTER|JOIN|LIMIT)(\\w+)?");

    public static final Pattern REPLACE_INTO_TABLE_NAME_PATTERN = Pattern.compile(
            "(?i)\\breplace\\s+into\\s+((\\w+\\.?){1,3})(?:\\s+as\\s+)?\\s*(?!SET|WHERE|ON|GROUP|HAVING|ORDER|LEFT|RIGHT|INNER|OUTER|JOIN|LIMIT)(\\w+)?");

    public static final Pattern INSERT_IGNORE_INTO_TABLE_NAME_PATTERN = Pattern.compile(
            "(?i)\\binsert\\s+ignore\\s+into\\s+((\\w+\\.?){1,3})(?:\\s+as\\s+)?\\s*(?!SET|WHERE|ON|GROUP|HAVING|ORDER|LEFT|RIGHT|INNER|OUTER|JOIN|LIMIT)(\\w+)?");


    public static final Pattern INSERT_INTO_COLUMN_PATTERN = Pattern.compile(
            "(?i)^\\bINSERT\\s+INTO\\s+((?:[\"`\\[]?\\w+[\"`\\]]?\\.){0,2}[\"`\\[]?\\w+[\"`\\]]?)\\s*\\(\\s*((?:[\"`\\[]?\\w+[\"`\\]]?\\s*,?\\s*)*)\\s*\\)$"
    );

    public static final Pattern REPLACE_INTO_COLUMN_PATTERN = Pattern.compile(
            "(?i)^\\bREPLACE\\s+INTO\\s+((?:[\"`\\[]?\\w+[\"`\\]]?\\.){0,2}[\"`\\[]?\\w+[\"`\\]]?)\\s*\\(\\s*((?:[\"`\\[]?\\w+[\"`\\]]?\\s*,?\\s*)*)\\s*\\)$"
    );

    public static final Pattern INSERT_IGNORE_INTO_COLUMN_PATTERN = Pattern.compile(
            "(?i)^\\bINSERT\\s+IGNORE\\s+INTO\\s+((?:[\"`\\[]?\\w+[\"`\\]]?\\.){0,2}[\"`\\[]?\\w+[\"`\\]]?)\\s*\\(\\s*((?:[\"`\\[]?\\w+[\"`\\]]?\\s*,?\\s*)*)\\s*\\)$"
    );


    public static final Pattern INSERT_SQL_BLOCK_PATTERN = Pattern.compile("(?i)INSERT\\s+INTO\\s*([\\w.]+)?\\s*\\(([^)]*)\\)\\s*VALUES\\s*\\(([^)]*)\\)");


    public static final Pattern UPDATE_SET_COLUMN_PATTERN = Pattern.compile(
            "(?i)\\bUPDATE\\s+((?:[\"\\[]?\\w+[\"\\]]?\\.)*[\"\\[]?\\w+[\"\\]]?)\\s*(?:\\s+(AS\\s+)?(\\w+))?\\s+SET\\s*((?:[\"\\[]?\\w+[\"\\]]?\\s*=\\s*[^,]+\\s*,\\s*)*[\"\\[]?\\w+[\"\\]]?\\s*=\\s*[^,]+\\s*)?"
    );


    public static final Pattern UPDATE_TABLE_NAME_PATTERN = Pattern.compile(
            "(?i)\\bupdate\\s+((\\w+\\.?){1,3})(?:\\s+as\\s+)?\\s*(?!SET|WHERE|ON|GROUP|HAVING|ORDER|LEFT|RIGHT|INNER|OUTER|JOIN|LIMIT)(\\w+)?");

    public static final Pattern UPDATE_SQL_BLOCK_PATTERN = Pattern.compile("(?i)UPDATE\\s+([\\w.]+)\\s*(?:AS\\s+)?(\\w+)?\\s+SET\\s+([^=]+\\s*=\\s*[^\\s,]+)?\\s*(WHERE\\s+.*)?");


    public static final Pattern DELETE_TABLE_NAME_PATTERN = Pattern.compile(
            "(?i)\\bdelete\\s+from\\s+((\\w+\\.?){1,3})(?:\\s+as\\s+)?\\s*(?!SET|WHERE|ON|GROUP|HAVING|ORDER|LEFT|RIGHT|INNER|OUTER|JOIN|LIMIT)(\\w+)?");
    public static final Pattern DELETE_PATTERN = Pattern.compile("(?i)^DELETE\\s+FROM\\s+");
    public static final Pattern UPDATE_PATTERN = Pattern.compile("(?i)\\bupdate\\s+(\\w+)");
    public static final Pattern INSERT_PATTERN = Pattern.compile("(?i)\\binsert\\s+into\\s+(\\w+)");


    public static boolean matchesPattern(String sql, Pattern pattern) {
        Matcher matcher = pattern.matcher(sql);
        return matcher.find();
    }


    public static boolean matchesAnyPattern(String sql, Pattern... patterns) {
        List<Pattern> patternList = Stream.of(Objects.requireNonNull(patterns)).toList();
        for (Pattern pattern : patternList) {
            if (matchesPattern(sql, pattern)) {
                return true;
            }
        }
        return false;
    }


    public static Map<String, String> extractTableNameAndAlias(String sql) {
        Map<String, String> result = extractTableNamesWithPattern(sql, SELECT_TABLE_NAME_PATTERN);
        if (!result.isEmpty()) {
            return result;
        }

        result = extractTableNamesWithPattern(sql, INSERT_TABLE_NAME_PATTERN);
        if (!result.isEmpty()) {
            return result;
        }

        result = extractTableNamesWithPattern(sql, UPDATE_TABLE_NAME_PATTERN);
        if (!result.isEmpty()) {
            return result;
        }

        result = extractTableNamesWithPattern(sql, DELETE_TABLE_NAME_PATTERN);
        if (!result.isEmpty()) {
            return result;
        }
        return null;
    }

    public static List<String> extractInsertIntoTableColumns(String sql) {
        Matcher insertIntoMatcher = INSERT_INTO_COLUMN_PATTERN.matcher(sql);
        boolean insertIntoMatcherFound = insertIntoMatcher.find();
        if (insertIntoMatcherFound) {
            String tableName = insertIntoMatcher.group(1);
            String columns = insertIntoMatcher.group(2);
            return List.of(tableName, columns);
        }
        Matcher insertIgnoreIntoMatcher = INSERT_IGNORE_INTO_COLUMN_PATTERN.matcher(sql);
        boolean insertIgnoreIntoMatcherFound = insertIgnoreIntoMatcher.find();
        if (insertIgnoreIntoMatcherFound) {
            String tableName = insertIgnoreIntoMatcher.group(1);
            String columns = insertIgnoreIntoMatcher.group(2);
            return List.of(tableName, columns);
        }
        Matcher replaceIntoMatcher = REPLACE_INTO_COLUMN_PATTERN.matcher(sql);
        boolean replaceIntoMatcherFound = replaceIntoMatcher.find();
        if (replaceIntoMatcherFound) {
            String tableName = replaceIntoMatcher.group(1);
            String columns = replaceIntoMatcher.group(2);
            return List.of(tableName, columns);
        }
        return null;
    }

    public static Map<String, String> extractTableNamesWithPattern(String sql, Pattern pattern) {
        Matcher matcher = pattern.matcher(sql);
        Map<String, String> tableAliasMap = new LinkedHashMap<>();

        while (matcher.find()) {
            String TABLE_NAME = matcher.group(1) != null ? matcher.group(1)
                    : matcher.group(4) != null ? matcher.group(4)
                    : matcher.group(7);

            String TABLE_ALIAS = null;
            if (matcher.groupCount() >= 3 && matcher.group(3) != null) {
                TABLE_ALIAS = matcher.group(3);
            } else if (matcher.groupCount() >= 6 && matcher.group(6) != null) {
                TABLE_ALIAS = matcher.group(6);
            }

            tableAliasMap.put(TABLE_NAME, TABLE_ALIAS);
        }

        return tableAliasMap;
    }


}
