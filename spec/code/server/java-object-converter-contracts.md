# Java Server Object Conversion Contracts

## 1. Purpose

Server-side object conversion must be centralized in converters. Controllers, services, adapters, facades, storage implementations, and plugin implementations must not define their own object-mapping methods. Centralized conversion prevents scattered field mappings, boundary-model leakage, and inconsistent transformations.

These contracts apply to structural conversion between Chat2DB project objects. They do not govern internal use of JDK, Spring, JDBC driver, ANTLR, JSON library, or other third-party types.

## 2. Definition of Object Conversion

Whenever one project object is transformed into another project object, the transformation is object conversion and must use a converter.

Typical conversions include:

1. Web request/response DTOs to and from domain requests/responses.
2. Domain requests/responses to and from domain models.
3. Domain models to and from storage models, entities, or records.
4. Gateway, CLI, MCP, local-storage, or plugin result objects to and from internal models.
5. Element conversion inside lists, pages, and tree structures.
6. Conversion of the same semantic object across layers when field names or types differ or light normalization is required.

The following are not mandatory converter use cases:

1. Value parsing such as `toString()`, enum `fromCode`, `fromValue`, or `fromName`.
2. Formatting strings, paths, SQL fragments, byte arrays, or raw JDBC values.
3. Adapting exceptions into HTTP error results.
4. Generic JDK collection helpers such as `toMap`, `toList`, or `toSet`.
5. Test fixtures, mocks, and assertion-object construction.

A helper that reads one project object and returns another is object conversion even when its name does not contain `convert`.

## 3. Converter Ownership

| Conversion | Converter Location | Notes |
| --- | --- | --- |
| HTTP DTO and domain contract object | `converter` package in `chat2db-community-web` | Controllers, web facades, and adapters call the converter |
| Domain contract object and domain model | `converter` package in `chat2db-community-domain-core` | Service implementations call the converter |
| Domain model and persistence object | `converter` package in `chat2db-community-storage` | Storage implementations call the converter |
| Plugin-internal models | `converter` package in the relevant `chat2db-community-plugins/*` module | Internal to the plugin and not exposed to web or domain-core |
| Shared SPI result conversion | A `converter` package or explicit converter type in `chat2db-community-spi` | May depend only on models allowed by SPI, domain-api, and tools |
| Tool-layer configuration objects | An explicit `*Converter` in `chat2db-community-tools` | Handles only tool-owned or third-party configuration objects |

Place new business-object conversions in a `converter` package and name them `XxxConverter`. Existing `*Converter` types outside a `converter` package must still follow pure-mapping rules.

## 4. Prohibited Ad Hoc Conversion

1. Non-converter classes must not add object-mapping methods named `toXxx`, `fromXxx`, `convertXxx`, or `xxx2yyy`.
2. Non-converter classes must not copy fields from a source object by creating `new Xxx()` and calling consecutive setters.
3. Non-converter classes must not assemble a target project object from another project object through a builder.
4. Non-converter classes must not use reflection-based copy helpers such as `BeanUtils.copyProperties`, `BeanUtil.copyProperties`, or `PropertyUtils.copyProperties` for project-object conversion.
5. Non-converter classes must not use `ObjectMapper.convertValue` for project-object conversion.
6. Non-converter classes must not perform JSON round trips such as `JSON.parseObject(JSON.toJSONString(source), Target.class)` for project-object conversion.
7. Non-converter classes must not declare MapStruct `@Mapper` types or call `Mappers.getMapper` directly.

Example:

```java
// Prohibited: a private conversion method in a controller.
private ModelConfigSaveRequest toModelConfigParam(WebModelConfigSaveRequest request) {
    ModelConfigSaveRequest param = new ModelConfigSaveRequest();
    param.setName(request.getName());
    return param;
}

// Allowed: the controller delegates conversion.
ModelConfigSaveRequest param = modelConfigWebConverter.request2param(request);
```

## 5. Pure-Mapping Rules

1. A converter performs field mapping, collection-element mapping, null handling, field-name conversion, and lightweight type conversion only.
2. A converter may contain MapStruct `@Mapping`, `@Mappings`, `@MappingTarget`, and a small number of default methods.
3. A converter may perform lightweight normalization required by the conversion, such as enum-name conversion, trimming, display masking, or an established encryption/decryption boundary.
4. A converter must not inject or call services, storage components, mappers, repositories, clients, or `ApplicationContext`.
5. A converter must not enforce permissions, execute state transitions, check object existence, or orchestrate cross-module business behavior.
6. A converter must not swallow an exception and return a default object. Conversion failures must surface as explicit exceptions.
7. Public converter method names must identify source and target, for example `request2param`, `model2response`, `storage2model`, or `toResponse`.

If conversion requires a database lookup, remote call, or runtime context, it is not pure object conversion. A business service must first obtain a complete source object and then pass it to a converter for structural mapping.

A converter that temporarily implements a conversion interface during migration is only a bridge. The interface must not be used to invoke business capabilities, and new code must not expand this pattern.

## 6. Call Boundaries

1. A controller handles HTTP binding, validation triggering, and response wrapping. It must not hand-build domain requests.
2. A web adapter performs protocol adaptation and call orchestration. It must not hand-build gateway, domain, or web model conversions.
3. A service implementation performs business orchestration and rule evaluation. It must not hand-build response, model, or entity conversions.
4. A storage implementation performs persistence calls. It must not hand-build domain-model and storage-object conversions.
5. A plugin implementation provides plugin capabilities. Plugin model conversion belongs in a plugin-local converter.
6. Across modules, callers may depend only on the target layer's public contract objects and converters owned by the caller's layer. They must not bypass converters to assemble another layer's internal models.

## 7. Allowed Exceptions

The following cases do not require a converter, but must remain narrowly scoped:

1. A static factory on an object enforces its own invariants without copying fields from another layer.
2. An enum or value-object parser returns its own type, such as `fromCode` or `fromValue`.
3. An SQL/JDBC raw-value processor returns `String`, `byte[]`, a primitive value, or a raw driver object.
4. An exception converter adapts an exception into an HTTP error result.
5. Test code constructs test data.
6. Startup assembly or configuration code creates beans or configuration property objects without mapping business objects.

Once an exception starts copying fields from one project object into another, move it into a converter.

## 8. Review Checklist

Object-conversion reviews must verify:

1. Non-converter production files do not use MapStruct `@Mapper` or `Mappers.getMapper`.
2. Non-converter files do not use reflection copying, JSON round trips, or `ObjectMapper.convertValue` for project-object conversion.
3. Non-converter files do not define `toXxx`, `fromXxx`, or `convertXxx` methods that return Chat2DB project objects.
4. Converter files do not depend on services, storage components, mappers, repositories, implementation classes, or Spring bean lookup.
5. `*Converter` and `*Convertor` types live in `converter` or `convertor` packages unless an explicit legacy exception applies.
6. Boundary classes do not use suspicious `new + set` mapping sequences.
7. Reflection copying, JSON round trips, or `ObjectMapper.convertValue` inside a converter has an explicit justification.

Interface methods, SQL completion/parser candidate construction, and SQL builder temporary objects are not automatic violations and require context-aware review.
