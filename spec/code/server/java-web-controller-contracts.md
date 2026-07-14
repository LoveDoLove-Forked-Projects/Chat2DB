# Java Web Controller Contracts

## 1. Purpose

A web controller adapts the HTTP boundary only and must not contain business rules. Controllers call business capabilities through service interfaces and return HTTP results through the standard result wrappers. Business decisions, object assembly, and external dependency calls must not be scattered through the web layer.

These contracts apply to `@RestController` and `@Controller` endpoints in `chat2db-community-web`. Filters, interceptors, exception handlers, converters, adapters, and asynchronous execution components are not controllers, but they must still follow their own module and object-conversion boundaries.

## 2. Business Naming Chain

Names from HTTP input through business service to HTTP output must share one semantic anchor:

```text
<Domain><Object><Action>
```

| Segment | Meaning | Examples |
| --- | --- | --- |
| `Domain` | Top-level business-domain prefix | `Sys`, `Db`, `Ai`, `Cli`, `Mcp`, `Task`, `Ops`, `Plugin` |
| `Object` | Business object or capability being operated on | `Datasource`, `Connection`, `Sql`, `Model`, `Chat`, `Permission` |
| `Action` | Business action | `Create`, `Update`, `Delete`, `Get`, `List`, `Execute`, `Test`, `Import`, `Export` |

Top-level domain meanings:

| Prefix | Meaning |
| --- | --- |
| `Sys` | System settings, accounts, permissions, OAuth, proxies, and runtime configuration |
| `Db` | Database connections, metadata, SQL, DDL/DML, tables, views, functions, procedures, triggers, and Redis data operations |
| `Ai` | AI chats, models, completion, RAG, embeddings, and AI-assisted schema capabilities |
| `Cli` | CLI and headless capabilities |
| `Mcp` | MCP protocols, tools, resources, and authorization |
| `Task` | Import/export, asynchronous tasks, and long-running workflows |
| `Ops` | Operation history, auditing, saved queries, and history records |
| `Plugin` | Plugin extension capabilities |

Naming rules:

1. Name controllers `<Domain><Object>Controller`, for example `DbDatasourceController`, `AiAiChatController`, or `SysSysPermissionController`.
2. Controller method names express only the action in lowerCamel form, for example `create`, `update`, `delete`, `get`, `list`, `execute`, or `test`.
3. Name web request DTOs `<Domain><Object><Action>Request`.
4. Name web action response DTOs `<Domain><Object><Action>Response`.
5. A web resource or view response may use `<Domain><Object>Response`, such as `DbDatasourceResponse` or `DbTableResponse`, but it still requires a business-domain prefix.
6. Name domain service interfaces `I<Domain><Object>Service` or `I<Domain><Object><Capability>Service`.
7. Name service implementations `<Domain><Object>ServiceImpl` or `<Domain><Object><Capability>ServiceImpl`.
8. Request, controller, service, implementation, and response names for one endpoint must expose the same `<Domain><Object><Action>` semantics.
9. Do not add unscoped generic names such as `SysSystemController`, `DbDataSourceController`, `CreateRequest`, `ExecuteResponse`, `ManagerService`, or `ConfigDTO`.
10. `Rdb`, `Redis`, `Database`, `DataSource`, `Table`, `View`, `Function`, `Procedure`, and `Trigger` are not top-level business domains. They belong to the `Db` domain.
11. Shared value objects may live under `model`, but must not masquerade as business requests or responses.

Example:

```java
public class DbDatasourceController {

    public DataResult<DbDatasourceCreateResponse> create(@Valid @RequestBody DbDatasourceCreateRequest request) {
        return DataResult.of(dbDatasourceService.create(request));
    }
}

public interface IDbDatasourceService {

    DbDatasourceCreateResponse create(DbDatasourceCreateRequest dbDatasourceCreateRequest);
}

public class DbDatasourceServiceImpl implements IDbDatasourceService {
}
```

## 3. Controller Responsibilities

A controller may only:

1. Declare HTTP routes, HTTP methods, request binding, and response types.
2. Trigger parameter validation through Bean Validation annotations such as `@Valid`, `@Validated`, and `@NotNull`.
3. Call web converters to transform web DTOs and domain requests or responses.
4. Call service interfaces under `ai.chat2db.community.domain.api.service`. Do not create a second business service layer under `web.api.service`.
5. Wrap service results in `ActionResult`, `DataResult<T>`, `ListResult<T>`, `WebPageResult<T>`, or the CLI-specific `CliResult<T>`.

A controller must not:

1. Evaluate business rules involving permissions, editions, environments, ownership, existence, default completion, state transitions, or cascading operations.
2. Call `GatewayUtil`, `WorkspaceStorageWebFacade`, adapters, task managers, storage implementations, mappers, repositories, implementation classes, or other business implementations directly.
3. Read or write files, database connections, OSS, remote HTTP services, thread pools, global configuration, or process-exit behavior directly.
4. Orchestrate a business workflow with `if/else`, `for/while`, or `try/catch` inside an endpoint.
5. Create business objects with `new`, assemble them with consecutive setters, or hand-write object conversion.
6. Catch an exception and return an empty list, empty success, default object, or raw error message.
7. Call one endpoint method from another endpoint or hide reusable business workflow in private controller helpers.

When an endpoint requires multi-step orchestration, add a domain-api service method with clear business semantics and implement the orchestration below that boundary. HTTP, CLI runtime, SSE, upload/download, and legacy web-facade bridges do not justify another `web.api.service` layer; move the capability behind an existing or new domain-api service interface.

## 4. Result Wrappers

Ordinary HTTP endpoints must use the standard result wrappers:

| Scenario | Return Type |
| --- | --- |
| Operation without response data | `ActionResult` |
| Single object, string, boolean, or ID | `DataResult<T>` |
| List | `ListResult<T>` |
| Page | `WebPageResult<T>` |
| CLI runtime HTTP API | `CliResult<T>` |

Ordinary endpoints must not return raw objects, collections, strings, booleans, maps, or business models.

Allowed exceptions:

1. SSE or streaming endpoints may return `SseEmitter`, but a dedicated adapter or service owns event, error, and completion semantics.
2. File downloads may return `ResponseEntity<Resource>` or write `HttpServletResponse` directly, but permissions, ownership, path resolution, and resource reading belong behind a service interface.
3. Receiving `MultipartFile` is HTTP binding. Storage, remote calls, and authorization after upload belong behind a service interface.
4. CLI runtime endpoints may use the CLI-specific `CliResult<T>` wrapper.
5. An HTML route in an `@Controller` may return a Spring MVC view-name `String`, but page selection, OAuth callbacks, cookies, redirects, and model attributes still belong behind service interfaces.

These exceptions must not expand into raw returns for ordinary business endpoints.

## 5. Service Call Boundary

Controllers may depend on business capabilities only through `IxxxService` interfaces under `ai.chat2db.community.domain.api.service`. The web module must not define or inject controller-service wrappers under `ai.chat2db.community.web.api.service`. Extend domain-api when a contract is missing.

Allowed controller dependencies:

1. Domain-api service interfaces.
2. Web converters or convertors.
3. HTTP binding types such as request/response DTOs, `MultipartFile`, and `HttpServletResponse`.
4. Standard result wrapper types.

Prohibited controller dependencies:

1. `*Impl` classes, mappers, repositories, DAOs, and storage implementations.
2. Any interface or implementation under `ai.chat2db.community.web.api.service`.
3. `GatewayUtil`, `WorkspaceStorageWebFacade`, task managers, web adapters, and other components that own business behavior or external calls.
4. `ApplicationContext#getBean`, reflection, class-name strings, or other mechanisms that bypass service interfaces.
5. Concrete domain-core, storage, SPI, or plugin implementation modules.

If a legacy web facade or adapter still owns a business entry point, move that capability behind a domain-api service first. A controller must not call it directly or hide it behind a web service wrapper.

## 6. Delegate Business Rules

The following behavior belongs in services rather than endpoint bodies:

1. Environment or edition checks such as `ConfigUtils.isDesktop()`, `isCommunity()`, or `isRelease()`.
2. User, organization, tenant, permission, or ownership decisions based on cookies, headers, or sessions.
3. Object existence, ownership, and delete/update authorization.
4. Business defaults for drivers, models, pages, schemas, and similar values.
5. Calls to gateway, AI, license, update, OSS, or remote HTTP services followed by result composition.
6. Batch import, export, delete, and cascading cleanup loops.
7. Retry, fallback, empty-result, or error-code decisions after an external dependency fails.

A controller may select a wrapper based on a service result, but it must not reinterpret the business result.

## 7. DTO and Conversion Boundaries

1. Controller inputs use web request DTOs rather than domain or storage models, except for explicit legacy compatibility contracts.
2. Controller outputs use standard wrappers. New endpoints should return web response DTOs. A historical endpoint may expose an existing domain-api model as a wrapper generic, but the controller must not assemble or mutate it.
3. Conversion between web DTOs and domain requests or responses belongs in converters in `chat2db-community-web`.
4. Controllers must not use `BeanUtils.copyProperties`, JSON round trips, `ObjectMapper.convertValue`, or hand-written setters for conversion. Thin delegation such as `xxxService.setXxx(...)` is not object conversion, but service implementations still follow the object-conversion contracts.

## 8. Review Checklist

Web controller reviews must verify:

1. Controller names use allowed top-level business-domain prefixes; legacy names are identified for migration.
2. Web request and response DTOs use allowed business-domain prefixes; legacy names are identified for migration.
3. Controller endpoints in `chat2db-community-web` return a standard wrapper or an explicit allowed exception.
4. Controllers do not depend directly on `GatewayUtil`, `WorkspaceStorageWebFacade`, adapters, storage, mappers, repositories, or implementations.
5. Controllers inject only domain-api service interfaces, converters, HTTP binding types, and result wrappers, with no `web.api.service` business wrapper layer.
6. Endpoint bodies do not call external dependencies, static business facades, `System.exit`, JDBC connections, OSS clients, or task managers directly.
7. Endpoint bodies do not contain suspicious business orchestration, object mutation, business-object construction, or excessive method length.
8. Endpoint methods do not call other endpoint methods or private controller helpers.
9. `@RequestBody` endpoints trigger Bean Validation through `@Valid` or `@Validated` where applicable.

Complex legacy endpoints require manual review even when their surface types satisfy these rules.
