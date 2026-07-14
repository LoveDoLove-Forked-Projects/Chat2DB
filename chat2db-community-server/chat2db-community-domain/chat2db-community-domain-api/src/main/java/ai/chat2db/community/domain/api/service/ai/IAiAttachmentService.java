package ai.chat2db.community.domain.api.service.ai;

import ai.chat2db.community.domain.api.model.ai.ChatAttachment;
import ai.chat2db.community.domain.api.model.request.ai.AiAttachmentParseRequest;
import ai.chat2db.community.domain.api.model.request.ai.AiLocalAttachmentParseRequest;

import java.util.List;

/**
 * Parses chat attachments and builds attachment context for AI requests.
 */
public interface IAiAttachmentService {

    /**
     * Parses a remote chat attachment.
     *
     * @param aiAttachmentParseRequest remote attachment parse parameters.
     * @return parsed chat attachment.
     */
    ChatAttachment parse(AiAttachmentParseRequest aiAttachmentParseRequest);

    /**
     * Parses a local chat attachment.
     *
     * @param aiLocalAttachmentParseRequest local attachment parse parameters.
     * @return parsed chat attachment.
     */
    ChatAttachment parse(AiLocalAttachmentParseRequest aiLocalAttachmentParseRequest);

    /**
     * Builds structured text context from chat attachments.
     *
     * @param attachments chat attachments to include.
     * @return structured attachment context.
     */
    String buildStructuredContext(List<ChatAttachment> attachments);

    /**
     * Checks whether the attachment list contains tabular content.
     *
     * @param attachments chat attachments to inspect.
     * @return true when a tabular attachment exists; false otherwise.
     */
    boolean hasTabularAttachment(List<ChatAttachment> attachments);

    /**
     * Checks whether any attachment is present.
     *
     * @param attachments chat attachments to inspect.
     * @return true when the list contains an attachment; false otherwise.
     */
    boolean hasAttachment(List<ChatAttachment> attachments);
}
