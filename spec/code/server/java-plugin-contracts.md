# Java Plugin Implementation Contracts

## 1. Purpose

Database plugins under `chat2db-community-plugins` are the only extension points for database-specific capabilities. Callers and reviewers must be able to determine which class is the primary plugin entry point, which capabilities it exposes, how it executes SQL, and how it manages resources and errors.

## 2. Primary Plugin Entry Point

1. Each database plugin module must have exactly one primary entry class: `XxxPlugin implements IPlugin`.
2. Name the primary entry class after the database with the `Plugin` suffix, for example `MysqlPlugin` or `PostgreSQLPlugin`.
3. `META-INF/services` must register the current interface name `ai.chat2db.spi.IPlugin`.
4. Do not use the obsolete service filename `ai.chat2db.spi.Plugin`.
5. A generic plugin may expose multiple `DBConfig` entries through `GenericPlugin#getDBConfigList()`, but it still has only one primary entry point.
6. Order `@Override` methods in the primary plugin class according to the `IPlugin` interface. Place helper methods after all overrides.

## 3. Syntax Capabilities

1. SQL parsing and SQL completion are database plugin capabilities and must not remain a second independent plugin-registration path.
2. `ISqlSyntaxPlugin` may remain as the syntax capability interface, but the primary path must obtain it from `IPlugin`.
3. `DefaultSqlSyntaxHandler` must read syntax capabilities only from `IPlugin#getSqlSyntaxPlugin()` and must not use an independent `ServiceLoader<ISqlSyntaxPlugin>`.
4. New plugins must not add a separate syntax service registration.
5. A temporarily retained `XxxSyntaxPlugin` must match the database type of its `XxxPlugin`.
6. A plugin without parser or completion support must return an explicit unsupported result rather than `null`.

## 4. DBManager Naming

1. A database manager that implements `IDBManager` must use the `XxxDBManager` name.
2. Do not use the obsolete `XxxDBManage` form.
3. Do not use vague names such as `XxxManage` for a database manager.
4. The concrete type returned by `IPlugin#getDBManager()` must follow `XxxDBManager` naming.
5. Abstract base classes in the hierarchy must use `XxxDBManager` or `XxxBaseDBManager`.

## 5. SQL Statements

1. Define all executable SQL as named constants.
2. Place SQL, commands, field names, error codes, method names, and template-fragment constants in the plugin module's `constant` package. Split them into focused `*Constants` types when needed.
3. Execution points must not contain raw SQL literals such as `"select ..."`, `"show ..."`, or `"drop ..."`.
4. Dynamic SQL templates must come from the `constant` package, for example `TABLE_DDL_SQL = "show create table %s"`.
5. SQL constant names must describe their purpose. Names such as `SQL1` or `TEMP_SQL` are prohibited.
6. SQL semantic fragments must also be constants. Do not build SQL in method bodies with fragments such as `" WHERE " + value` or `"select " + column + " from " + table`.
7. When combining SQL with `String.format`, `StringUtils.join`, `StringBuilder`, or similar APIs, the first template or fragment must be a named constant.
8. Prefer the relevant `XxxSqlBuilder` or shared `ISqlBuilder` capability for database-object DDL and DML construction.
9. `XxxDBManager` owns connection and execution behavior. Do not add `buildXxxSql` methods to it; delegate SQL generation to a builder.
10. SQL builders may combine fragments for preview or export, but every SQL fragment used in the composition must still be a named constant.
11. Runtime objects such as `INSTANCE`, loggers, caches, strategy chains, and immutable registries are not SQL-literal constants. Text that represents SQL, commands, fields, errors, or method names still belongs in the `constant` package.

## 6. Unified SQL Builder Entry Point

1. `ISqlBuilder` is the unified entry point for SQL construction and must not use generics.
2. The shared interface accepts only Community domain or SPI models. Dialect-private models such as Redis keys or MySQL routine parameters belong in private plugin builder methods and must not leak into SPI.
3. Split `ISqlBuilder` by SQL semantics: `identifier()`, `literal()`, `dql()`, `dml()`, `ddl()`, `dcl()`, `tcl()`, `metadata()`, `routine()`, `export()`, and `unsafe()`.
4. Split `ddl()` by object type: `database()`, `schema()`, `table()`, `column()`, `index()`, and `view()`.
5. Use `dql()` rather than `query()`. DQL methods use forms such as `buildSelectXxx`, `buildExplain`, `buildPageLimit`, and `buildOrderBy`.
6. SQL generation methods use `build + SQL verb + object`, for example `buildCreateTable`, `buildDropDatabase`, `buildSelectColumns`, or `buildShowCreateTable`.
7. Do not add inconsistent names such as `getXxxSql`, `listXxxSql`, `queryXxxSql`, or `selectXxxSql`. Do not preserve compatibility bridges for obsolete entry points; update callers in the same change.
8. `identifier()` and `literal()` generate SQL fragments rather than statements, so names such as `quoteXxx` and `formatXxx` are allowed.
9. SQL builder methods currently return SQL text as `String`. Execution points that need parameter binding use `PreparedStatement` in the executor or manager.
10. Quote DDL identifiers that cannot be parameterized through `identifier()` before combining user-controlled object names.
11. `unsafe()` is limited to raw user SQL or explicitly non-structurable cases, and each call site must make the source clear.
12. Parser text, completion candidates, dummy SQL, and syntax candidates are not executable SQL and do not have to move into `ISqlBuilder`. A DBManager must not execute them as operational SQL.

## 7. Resource Files

1. Plugin configuration, forms, templates, and other resource files belong in `src/main/resources`.
2. Do not place `.json`, `.xml`, or `.properties` resources under `src/main/java`.
3. Keep resource paths aligned with the reading class package, for example `ai/chat2db/plugin/mysql/mysql.json`.
4. A resource-read failure must throw immediately with the resource path and target type. Do not swallow the error and return `null`.

## 8. PreparedStatement

1. Prefer `PreparedStatement` whenever executable SQL includes external input or database, schema, table, column, or other object names.
2. Do not concatenate user-controlled input and execute it through `createStatement().execute(...)`.
3. When an identifier cannot be parameterized, use a dialect quote/format method and a named SQL template.
4. Bind `PreparedStatement` parameters in the same order as SQL placeholders.
5. Extract complex binding logic into helpers placed after override methods and ordered by first call.

## 9. Error and Return Semantics

1. Plugin execution failures must throw exceptions. Do not swallow errors.
2. A catch block may add database type, schema, table, or SQL template context, but it must preserve the cause and rethrow.
3. Unsupported capabilities return an explicit unsupported result or an empty collection according to the interface contract.
4. A failed query throws; a successful query with no rows returns an empty collection.
5. Do not use `null` to represent unsupported, failed, and empty states simultaneously.

## 10. Resource Management

1. Do not close a `Connection` owned by the calling context.
2. Manage `PreparedStatement` and `ResultSet` with try-with-resources.
3. Do not call `connection.createStatement()` repeatedly and lose statement references.
4. A `finally` block must not swallow the primary failure.

## 11. SPI Boundaries

1. `chat2db-community-spi` defines only extension points, shared abstractions, and database-independent defaults.
2. `chat2db-community-spi` and `chat2db-community-tools` must not contain central classes that branch on a concrete database type and execute database-specific operations.
3. Logic such as `if MYSQL -> DROP DATABASE ...` or `if POSTGRESQL -> DROP SCHEMA ...` is prohibited in SPI and tools.
4. Database-specific DDL, connection switching, identifier quoting, preview SQL, and execution SQL belong in the relevant plugin's `XxxDBManager`, `XxxMetaData`, or dialect processor.
5. Domain and web layers may decide whether a business capability is supported, but they must not build database-specific operational SQL. They call plugin capabilities for SQL previews.
6. Cross-database utilities may process database-independent strings, JDBC types, or parsing flows, but they must not own concrete `DBType` branch semantics.

## 12. Review Checklist

Plugin reviews must verify:

1. Each plugin module has exactly one `IPlugin` entry point.
2. Service registration uses `ai.chat2db.spi.IPlugin` and does not retain `ai.chat2db.spi.Plugin`.
3. The primary path does not depend on an obsolete independent syntax service registration.
4. `IDBManager` implementations use `XxxDBManager` naming.
5. `XxxPlugin` overrides follow `IPlugin` method order.
6. Execution points do not contain raw SQL literals.
7. Inline SQL strings do not participate directly in concatenation, `String.format`, `StringUtils.join`, or `StringBuilder` composition.
8. Dynamic SQL is not executed through unsafe `createStatement()` calls.
9. SPI and tools do not centralize database-specific operational SQL branches.
10. Plugin resources are not stored under `src/main/java`.
11. `XxxDBManager` does not add SQL-construction methods.
12. `ISqlBuilder` does not use generics.
13. SQL builder methods follow `build + SQL verb + object` naming.
14. SQL builder sub-entry points use `dql()` rather than `query()`.
15. Production plugin classes outside `constant` packages do not declare text constants for SQL, commands, fields, errors, or method names.

These checks do not replace Java compilation or code review. SQL constants, prepared statements, and complex dialect identifier handling require context-aware review.
