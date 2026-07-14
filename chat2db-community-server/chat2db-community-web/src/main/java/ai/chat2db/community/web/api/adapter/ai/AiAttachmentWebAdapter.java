package ai.chat2db.community.web.api.adapter.ai;

import ai.chat2db.community.domain.api.model.ai.ChatAttachment;
import ai.chat2db.community.domain.api.model.request.ai.AiAttachmentParseRequest;
import ai.chat2db.community.domain.api.model.request.ai.AiLocalAttachmentParseRequest;
import ai.chat2db.community.domain.api.service.ai.IAiAttachmentService;
import ai.chat2db.community.domain.api.service.ai.IAiAttachmentParseService;
import ai.chat2db.community.tools.exception.BusinessException;
import ai.chat2db.community.web.api.model.request.ai.ParseLocalAttachmentRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class AiAttachmentWebAdapter
        implements IAiAttachmentParseService<MultipartFile, ParseLocalAttachmentRequest, ChatAttachment> {

    private final IAiAttachmentService aiAttachmentService;

    public AiAttachmentWebAdapter(IAiAttachmentService aiAttachmentService) {
        this.aiAttachmentService = aiAttachmentService;
    }

    public ChatAttachment parse(MultipartFile file) {
        return parseUpload(file);
    }

    @Override
    public ChatAttachment parseUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("ai.attachment.inputStreamRequired");
        }
        try {
            AiAttachmentParseRequest param = new AiAttachmentParseRequest();
            param.setFileName(StringUtils.defaultIfBlank(file.getOriginalFilename(), "attachment"));
            param.setInputStream(file.getInputStream());
            return aiAttachmentService.parse(param);
        } catch (IOException e) {
            throw new BusinessException("ai.attachment.parseFailed", new Object[]{
                    file == null ? "attachment" : StringUtils.defaultIfBlank(file.getOriginalFilename(), "attachment"),
                    e.getMessage()
            }, e);
        }
    }

    public ChatAttachment parse(ParseLocalAttachmentRequest request) {
        return parseLocal(request);
    }

    @Override
    public ChatAttachment parseLocal(ParseLocalAttachmentRequest request) {
        AiLocalAttachmentParseRequest param = new AiLocalAttachmentParseRequest();
        if (request != null) {
            param.setFileName(request.getFileName());
            param.setFilePath(request.getFilePath());
        }
        return aiAttachmentService.parse(param);
    }
}
