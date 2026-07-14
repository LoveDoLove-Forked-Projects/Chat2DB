package ai.chat2db.community.domain.api.model.completion;


public record SqlCompletionFunctionSpec(String name,
                                        String parameters,
                                        String returnType) {
}
