package ai.chat2db.community.web.api.adapter.ai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionChunk;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionChunk.ChunkChoice;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage.Role;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompatibleOpenAiStreamFunctionCallingHelperTest {

    @Test
    void shouldAppendReasoningContentWhenMergingChunks() {
        CompatibleOpenAiStreamFunctionCallingHelper helper = new CompatibleOpenAiStreamFunctionCallingHelper();
        ChatCompletionChunk previous = chunkWithReasoning("Need schema ");
        ChatCompletionChunk current = chunkWithReasoning("before querying.");

        ChatCompletionChunk merged = helper.merge(previous, current);

        assertEquals("Need schema before querying.", merged.choices().get(0).delta().reasoningContent());
    }

    private ChatCompletionChunk chunkWithReasoning(String reasoningContent) {
        ChatCompletionMessage message = new ChatCompletionMessage("", Role.ASSISTANT, null, null, null, null, null,
                null, reasoningContent);
        ChunkChoice choice = new ChunkChoice(null, 0, message, null);
        return new ChatCompletionChunk("chatcmpl_1", List.of(choice), 1L, "deepseek", null, null,
                "chat.completion.chunk", null);
    }
}
