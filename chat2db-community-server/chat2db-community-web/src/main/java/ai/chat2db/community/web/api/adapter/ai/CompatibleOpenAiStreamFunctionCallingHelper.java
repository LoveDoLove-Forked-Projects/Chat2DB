package ai.chat2db.community.web.api.adapter.ai;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletion;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletion.Choice;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionChunk;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionChunk.ChunkChoice;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionFinishReason;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage.ChatCompletionFunction;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage.Role;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage.ToolCall;
import org.springframework.ai.openai.api.OpenAiApi.LogProbs;
import org.springframework.ai.openai.api.OpenAiApi.Usage;
import org.springframework.ai.openai.api.OpenAiStreamFunctionCallingHelper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CompatibleOpenAiStreamFunctionCallingHelper extends OpenAiStreamFunctionCallingHelper {

    @Override
    public ChatCompletionChunk merge(ChatCompletionChunk previous, ChatCompletionChunk current) {

        if (previous == null) {
            return current;
        }

        if (current == null) {
            return previous;
        }

        String id = current.id() != null ? current.id() : previous.id();
        Long created = current.created() != null ? current.created() : previous.created();
        String model = current.model() != null ? current.model() : previous.model();
        String serviceTier = current.serviceTier() != null ? current.serviceTier() : previous.serviceTier();
        String systemFingerprint = current.systemFingerprint() != null
                ? current.systemFingerprint() : previous.systemFingerprint();
        String object = current.object() != null ? current.object() : previous.object();
        Usage usage = current.usage() != null ? current.usage() : previous.usage();

        ChunkChoice previousChoice0 = CollectionUtils.isEmpty(previous.choices()) ? null : previous.choices().get(0);
        ChunkChoice currentChoice0 = CollectionUtils.isEmpty(current.choices()) ? null : current.choices().get(0);

        ChunkChoice choice = mergeChoice(previousChoice0, currentChoice0);
        List<ChunkChoice> chunkChoices = choice == null ? List.of() : List.of(choice);
        return new ChatCompletionChunk(id, chunkChoices, created, model, serviceTier, systemFingerprint, object, usage);
    }

    private ChunkChoice mergeChoice(ChunkChoice previous, ChunkChoice current) {
        if (previous == null) {
            return current;
        }

        if (current == null) {
            return previous;
        }

        ChatCompletionFinishReason finishReason = current.finishReason() != null
                ? current.finishReason() : previous.finishReason();
        Integer index = current.index() != null ? current.index() : previous.index();
        ChatCompletionMessage message = mergeMessage(previous.delta(), current.delta());
        LogProbs logprobs = current.logprobs() != null ? current.logprobs() : previous.logprobs();
        return new ChunkChoice(finishReason, index, message, logprobs);
    }

    private ChatCompletionMessage mergeMessage(ChatCompletionMessage previous, ChatCompletionMessage current) {
        String content = mergeText(previous.content(), current.content());
        String reasoningContent = mergeText(previous.reasoningContent(), current.reasoningContent());
        Role role = current.role() != null ? current.role() : previous.role();
        role = role != null ? role : Role.ASSISTANT;
        String name = current.name() != null ? current.name() : previous.name();
        String toolCallId = current.toolCallId() != null ? current.toolCallId() : previous.toolCallId();
        String refusal = current.refusal() != null ? current.refusal() : previous.refusal();
        ChatCompletionMessage.AudioOutput audioOutput = current.audioOutput() != null
                ? current.audioOutput() : previous.audioOutput();
        List<ChatCompletionMessage.Annotation> annotations = current.annotations() != null
                ? current.annotations() : previous.annotations();

        List<ToolCall> toolCalls = new ArrayList<>();
        ToolCall lastPreviousToolCall = null;
        if (previous.toolCalls() != null && !previous.toolCalls().isEmpty()) {
            lastPreviousToolCall = previous.toolCalls().get(previous.toolCalls().size() - 1);
            if (previous.toolCalls().size() > 1) {
                toolCalls.addAll(previous.toolCalls().subList(0, previous.toolCalls().size() - 1));
            }
        }

        if (current.toolCalls() != null && !current.toolCalls().isEmpty()) {
            if (current.toolCalls().size() > 1) {
                throw new IllegalStateException("Currently only one tool call is supported per message!");
            }
            ToolCall currentToolCall = current.toolCalls().get(0);
            if (shouldAppendToPreviousToolCall(lastPreviousToolCall, currentToolCall)) {
                toolCalls.add(mergeToolCall(lastPreviousToolCall, currentToolCall));
            } else if (StringUtils.hasText(currentToolCall.id())) {
                if (lastPreviousToolCall != null) {
                    toolCalls.add(lastPreviousToolCall);
                }
                toolCalls.add(currentToolCall);
            } else {
                toolCalls.add(mergeToolCall(lastPreviousToolCall, currentToolCall));
            }
        } else if (lastPreviousToolCall != null) {
            toolCalls.add(lastPreviousToolCall);
        }

        return new ChatCompletionMessage(content, role, name, toolCallId, toolCalls, refusal, audioOutput, annotations,
                reasoningContent);
    }

    private String mergeText(String previous, String current) {
        if (current == null) {
            return previous != null ? previous : "";
        }
        if (!StringUtils.hasText(previous)) {
            return current;
        }
        if (current.startsWith(previous)) {
            return current;
        }
        return previous + current;
    }

    private boolean shouldAppendToPreviousToolCall(ToolCall previous, ToolCall current) {
        if (previous == null || current == null) {
            return false;
        }
        if (!StringUtils.hasText(current.id())) {
            return true;
        }
        return StringUtils.hasText(previous.id()) && Objects.equals(previous.id(), current.id());
    }

    private ToolCall mergeToolCall(ToolCall previous, ToolCall current) {
        if (previous == null) {
            return current;
        }
        String id = StringUtils.hasText(current.id()) ? current.id() : previous.id();
        String type = current.type() != null ? current.type() : previous.type();
        ChatCompletionFunction function = mergeFunction(previous.function(), current.function());
        return new ToolCall(id, type, function);
    }

    private ChatCompletionFunction mergeFunction(ChatCompletionFunction previous, ChatCompletionFunction current) {
        if (previous == null) {
            return current;
        }
        String name = StringUtils.hasText(current.name()) ? current.name() : previous.name();
        StringBuilder arguments = new StringBuilder();
        if (previous.arguments() != null) {
            arguments.append(previous.arguments());
        }
        if (current.arguments() != null) {
            arguments.append(current.arguments());
        }
        return new ChatCompletionFunction(name, arguments.toString());
    }

    @Override
    public ChatCompletion chunkToChatCompletion(ChatCompletionChunk chunk) {
        List<Choice> choices = chunk.choices()
                .stream()
                .map(chunkChoice -> new Choice(chunkChoice.finishReason(), chunkChoice.index(), chunkChoice.delta(),
                        chunkChoice.logprobs()))
                .toList();

        return new OpenAiApi.ChatCompletion(chunk.id(), choices, chunk.created(), chunk.model(), chunk.serviceTier(),
                chunk.systemFingerprint(), "chat.completion", null);
    }
}
