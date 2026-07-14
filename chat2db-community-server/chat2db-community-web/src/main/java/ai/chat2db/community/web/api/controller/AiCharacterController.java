package ai.chat2db.community.web.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.chat2db.community.domain.api.service.ai.IAiCharacterService;
import ai.chat2db.community.tools.wrapper.result.DataResult;
import ai.chat2db.community.web.api.model.request.character.CharacterHandleRequest;
import jakarta.validation.Valid;

/**
 * Manages AI character configuration endpoints.
 */
@RestController
@RequestMapping("/api/character")
public class AiCharacterController {

    private final IAiCharacterService characterService;

    public AiCharacterController(IAiCharacterService characterService) {
        this.characterService = characterService;
    }

    /**
     * Handles character handler for AI character configuration.
     * <p>
     * Endpoint: {@code POST /api/character/handler}.
     *
     * @param request request payload or query parameters for the operation.
     * @return data result containing string.
     */
    @PostMapping("/handler")
    public DataResult<String> characterHandler(@Valid @RequestBody CharacterHandleRequest request) {
        return DataResult.of(characterService.handle(request.getText()));
    }
}
