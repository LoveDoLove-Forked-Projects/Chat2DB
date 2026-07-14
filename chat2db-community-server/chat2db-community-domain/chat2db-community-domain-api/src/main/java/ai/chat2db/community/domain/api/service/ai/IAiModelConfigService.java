package ai.chat2db.community.domain.api.service.ai;

import ai.chat2db.community.domain.api.model.ai.AiModelCatalogItem;
import ai.chat2db.community.domain.api.model.ai.AiModelConfigResponse;
import ai.chat2db.community.domain.api.model.ai.AiModelOptionItem;
import ai.chat2db.community.domain.api.model.ai.AiRuntimeModel;
import ai.chat2db.community.domain.api.model.ai.ModelConfigTestResponse;
import ai.chat2db.community.domain.api.model.request.ai.AiChatRuntimeResolveRequest;
import ai.chat2db.community.domain.api.model.request.ai.AiModelConfigSaveRequest;

import java.util.List;

/**
 * Manages current-user AI model configuration.
 */
public interface IAiModelConfigService {

    /**
     * Lists preset AI model catalog entries.
     *
     * @return preset model catalog entries.
     */
    List<AiModelCatalogItem> listPresetModels();

    /**
     * Lists selectable AI model options.
     *
     * @return model option entries.
     */
    List<AiModelOptionItem> listModelOptions();

    /**
     * Lists model configurations owned by the current user.
     *
     * @return current-user model configurations.
     */
    List<AiModelConfigResponse> listCurrentUserConfigs();

    /**
     * Saves a model configuration for the current user.
     *
     * @param aiModelConfigSaveRequest model configuration save parameters.
     * @return saved model configuration.
     */
    AiModelConfigResponse saveCurrentUserConfig(AiModelConfigSaveRequest aiModelConfigSaveRequest);

    /**
     * Deletes a model configuration owned by the current user.
     *
     * @param id model configuration identifier.
     */
    void deleteCurrentUserConfig(String id);

    /**
     * Tests whether a model configuration can be used.
     *
     * @param aiModelConfigSaveRequest model configuration test parameters.
     * @return model configuration test response.
     */
    ModelConfigTestResponse testModelConfig(AiModelConfigSaveRequest aiModelConfigSaveRequest);

    /**
     * Resolves the runtime model used by a chat request.
     *
     * @param aiChatRuntimeResolveRequest chat runtime resolution parameters.
     * @return resolved runtime model.
     */
    AiRuntimeModel resolveRuntimeModel(AiChatRuntimeResolveRequest aiChatRuntimeResolveRequest);
}
