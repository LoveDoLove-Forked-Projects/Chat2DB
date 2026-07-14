# Java Implementation Contracts

## 1. Purpose

An `Impl` class is a replaceable implementation of an interface contract, not a new contract entry point. Callers and reviewers must be able to determine quickly which primary interface it implements, whether all entry methods are present, where helper logic lives, and whether failures are exposed correctly.

## 2. Primary Interface and Naming

1. A class that implements business, storage, plugin, or adapter behavior must explicitly implement an interface.
2. Implementation classes must use the `XxxImpl` suffix.
3. The primary interface for `XxxImpl` must be `IXxx` or `Xxx`. For example, `DataSourceServiceImpl implements IDataSourceService`.
4. An unrelated interface, empty interface, marker interface, or framework interface must not masquerade as the primary interface.
5. When a class implements multiple interfaces, one primary interface must be identifiable. Other interfaces may provide only cross-cutting capabilities such as `AutoCloseable`, `Serializable`, lifecycle hooks, or framework extensions.
6. The `implements` clause must appear in the class declaration. Inheritance, dynamic proxies, or runtime registration must not hide the primary interface relationship.

Allowed exceptions:

1. An abstract base class may omit the `Impl` suffix, but it must not be injected across modules as a business contract.
2. A framework implementation may implement a framework interface. If its class name uses the `XxxImpl` suffix, it must still have a clear primary interface or an explicit review exception.

## 3. Override Method Layout

1. Place the `@Override` method section after fields and constructors and before helper methods.
2. Order `@Override` methods according to their order in the primary interface.
3. When a method is added to the primary interface, insert the matching override at the corresponding location rather than appending it to the end of the class.
4. For multiple interfaces, place primary-interface overrides first in interface order, followed by overrides for cross-cutting interfaces.
5. Do not place private helpers, internal business methods, or temporary debugging methods between override methods.

Example:

```java
public class DataSourceServiceImpl implements IDataSourceService {

    private final DataSourceConverter dataSourceConverter;

    @Override
    public void preConnect(DataSourcePreConnectRequest dataSourcePreConnectRequest) {
        validateDesktopPreConnect(dataSourcePreConnectRequest);
    }

    @Override
    public List<Database> connect(Long id) {
        return queryDatabases(id);
    }

    private void validateDesktopPreConnect(DataSourcePreConnectRequest dataSourcePreConnectRequest) {
    }

    private List<Database> queryDatabases(Long id) {
    }
}
```

## 4. Helper Method Layout

1. Place all non-override helper methods after the override section.
2. Order helpers by the first time an override method calls them.
3. If one helper is called only by another helper, place it after its caller.
4. Do not reorder helpers alphabetically or by complexity, visibility, or historical addition order.
5. If a helper begins to provide an independent business capability, extract it behind an explicit interface or component instead of continuing to grow one `Impl` class.

## 5. Failure and Return Semantics

1. Throw immediately when execution fails. Do not swallow errors.
2. Do not log an exception and continue execution.
3. Do not hide failures with `null`, empty collections, default objects, `Optional.empty()`, or success wrappers.
4. Distinguish a normal empty business result from an execution failure. Empty collections, empty optionals, and default objects may represent only semantics explicitly declared by the interface.
5. An exception may be caught to add context, but it must then be rethrown.
6. A wrapped exception must retain the original cause unless the original exception already contains complete context and is rethrown unchanged.
7. Exception context must include at least the business action. For external dependencies, include the dependency name. For key objects, include a sanitized parameter summary.
8. An implementation must not convert failures into HTTP wrappers, generic result wrappers, or compatibility responses. HTTP compatibility belongs at the web/controller boundary.
9. Explicit best-effort cases such as editor hints, asynchronous audit logs, cache warmup, temporary-file cleanup, or non-critical context enrichment may degrade gracefully. Add `// impl-contract: best-effort - <reason>` or `// impl-contract: fallback - <strategy>` immediately before the relevant `catch` block.

Recommended exception form:

```java
try {
    return gatewayClient.queryDataSource(dataSourceId);
} catch (Exception e) {
    throw new BusinessException("Failed to query datasource from gateway, dataSourceId=" + dataSourceId, e);
}
```

## 6. Dependencies and Side Effects

1. Prefer injected interfaces when an `Impl` class depends on another business capability. Do not inject another `XxxImpl` directly.
2. Do not silently modify global state, thread context, or caches inside override methods. If a side effect is required, make it visible in the method name, failure behavior, and cleanup logic.
3. Release external resources on both success and failure paths.
4. A `finally` block must not swallow the primary failure. A cleanup failure must be rethrown with context or recorded as a suppressed exception.

## 7. Review Checklist

Implementation reviews must verify:

1. Every `*Impl.java` under `src/main/java` explicitly implements an interface.
2. `XxxImpl` implements the matching `IXxx` or `Xxx` primary interface.
3. The primary interface is not empty and is not a marker interface.
4. Override methods follow primary-interface method order.
5. Non-override methods appear after the complete override section.
6. Helpers follow first-call order from override methods.
7. Catch blocks do not only log, return `null`, return empty values, or return defaults.
8. Any `impl-contract: best-effort` or `impl-contract: fallback` annotation has a concrete and defensible reason.

Complex generic hierarchies, inherited interfaces, framework callbacks, and legacy multi-interface classes require manual review.
