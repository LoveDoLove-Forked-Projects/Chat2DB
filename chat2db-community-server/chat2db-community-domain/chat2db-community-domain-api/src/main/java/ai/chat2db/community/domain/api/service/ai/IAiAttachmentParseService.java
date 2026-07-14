package ai.chat2db.community.domain.api.service.ai;

/**
 * Parses AI chat attachments from transport-specific request types.
 *
 */
public interface IAiAttachmentParseService<U, L, A> {

    /**
     * Parses an uploaded attachment.
     *
     * @param file uploaded attachment.
     * @return parsed attachment.
     */
    A parseUpload(U file);

    /**
     * Parses a local attachment reference.
     *
     * @param request local attachment parse request.
     * @return parsed attachment.
     */
    A parseLocal(L request);
}
