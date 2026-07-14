package ai.chat2db.community.domain.core.impl.ai;

import ai.chat2db.community.domain.api.model.ai.ChatAttachment;
import ai.chat2db.community.domain.api.model.request.ai.AiAttachmentParseRequest;
import ai.chat2db.community.domain.api.model.request.ai.AiLocalAttachmentParseRequest;
import ai.chat2db.community.domain.api.service.ai.IAiAttachmentService;
import ai.chat2db.community.tools.exception.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class AiAttachmentServiceImpl implements IAiAttachmentService {

    private static final int MAX_CONTENT_LENGTH = 12000;
    private static final int MAX_CONTEXT_LENGTH = 24000;
    private static final int MAX_SHEET_ROWS = 100;
    private static final int MAX_SHEET_COLUMNS = 20;
    private static final Set<String> DOCUMENT_EXTENSIONS = Set.of("pdf", "doc", "docx", "md", "txt", "json");
    private static final Set<String> TABULAR_EXTENSIONS = Set.of("csv", "xls", "xlsx");
    private static final Set<String> SUPPORTED_EXTENSIONS;

    static {
        Set<String> supportedExtensions = new LinkedHashSet<>();
        supportedExtensions.addAll(DOCUMENT_EXTENSIONS);
        supportedExtensions.addAll(TABULAR_EXTENSIONS);
        SUPPORTED_EXTENSIONS = supportedExtensions;
    }

    public ChatAttachment parse(AiAttachmentParseRequest param) {
        if (param == null || param.getInputStream() == null) {
            throw new BusinessException("ai.attachment.inputStreamRequired");
        }
        String fileName = StringUtils.defaultIfBlank(param.getFileName(), "attachment");
        try (InputStream inputStream = param.getInputStream()) {
            return parse(fileName, inputStream);
        } catch (IOException e) {
            throw new BusinessException("ai.attachment.parseFailed", new Object[]{fileName, e.getMessage()}, e);
        }
    }

    public ChatAttachment parse(AiLocalAttachmentParseRequest param) {
        if (param == null || StringUtils.isBlank(param.getFilePath())) {
            throw new BusinessException("ai.attachment.filePathRequired");
        }
        File file = new File(param.getFilePath().trim());
        if (!file.exists() || !file.isFile()) {
            throw new BusinessException("ai.attachment.fileNotFound");
        }
        String fileName = StringUtils.defaultIfBlank(param.getFileName(), file.getName());
        try (InputStream inputStream = new FileInputStream(file)) {
            return parse(fileName, inputStream);
        } catch (IOException e) {
            throw new BusinessException("ai.attachment.localParseFailed", new Object[]{file.getPath(), e.getMessage()}, e);
        }
    }

    public String buildStructuredContext(List<ChatAttachment> attachments) {
        if (CollectionUtils.isEmpty(attachments)) {
            return null;
        }
        StringBuilder builder = new StringBuilder(4096);
        builder.append("Uploaded file context for the current conversation. ");
        builder.append("Treat it as user-provided evidence. ");
        builder.append("If the parsed content is truncated, say so when it affects certainty.\n");

        int remaining = MAX_CONTEXT_LENGTH;
        int index = 1;
        for (ChatAttachment attachment : attachments) {
            if (attachment == null || StringUtils.isBlank(attachment.getContent())) {
                continue;
            }
            String content = normalizeText(attachment.getContent());
            if (StringUtils.isBlank(content)) {
                continue;
            }
            String truncatedContent = content;
            if (truncatedContent.length() > remaining) {
                truncatedContent = truncatedContent.substring(0, remaining);
            }
            if (StringUtils.isBlank(truncatedContent)) {
                break;
            }
            builder.append("\n### Attachment ").append(index++).append("\n");
            builder.append("- File name: ").append(StringUtils.defaultIfBlank(attachment.getFileName(), "unknown")).append("\n");
            builder.append("- File type: ").append(StringUtils.defaultIfBlank(attachment.getFileType(), "unknown")).append("\n");
            builder.append("- Content category: ").append(StringUtils.defaultIfBlank(attachment.getContentCategory(), "DOCUMENT")).append("\n");
            builder.append("- Parsed content length: ").append(attachment.getContentLength() == null ? truncatedContent.length() : attachment.getContentLength()).append("\n");
            builder.append("- Truncated: ").append(Boolean.TRUE.equals(attachment.getTruncated()) ? "true" : "false").append("\n");
            builder.append("```text\n");
            builder.append(truncatedContent);
            if (truncatedContent.length() < content.length()) {
                builder.append("\n...[truncated due to context limit]");
            }
            builder.append("\n```\n");
            remaining -= truncatedContent.length();
            if (remaining <= 0) {
                break;
            }
        }
        return index == 1 ? null : builder.toString();
    }

    public boolean hasTabularAttachment(List<ChatAttachment> attachments) {
        return attachments != null && attachments.stream()
                .filter(item -> item != null)
                .anyMatch(item -> "TABULAR".equalsIgnoreCase(item.getContentCategory()));
    }

    public boolean hasAttachment(List<ChatAttachment> attachments) {
        return attachments != null && attachments.stream()
                .anyMatch(item -> item != null && StringUtils.isNotBlank(item.getContent()));
    }

    private ChatAttachment parse(String fileName, InputStream inputStream) throws IOException {
        String extension = StringUtils.lowerCase(FilenameUtils.getExtension(fileName));
        validateExtension(extension);

        String content = switch (extension) {
            case "pdf" -> parsePdf(inputStream);
            case "docx" -> parseDocx(inputStream);
            case "doc" -> parseDoc(inputStream);
            case "csv" -> parseCsv(inputStream);
            case "xls", "xlsx" -> parseWorkbook(inputStream);
            case "md", "txt", "json" -> readPlainText(inputStream);
            default -> throw new BusinessException("ai.attachment.unsupportedFileType");
        };

        String normalizedContent = normalizeText(content);
        if (StringUtils.isBlank(normalizedContent)) {
            throw new BusinessException("ai.attachment.emptyContent");
        }

        boolean truncated = normalizedContent.length() > MAX_CONTENT_LENGTH;
        String finalContent = truncated ? normalizedContent.substring(0, MAX_CONTENT_LENGTH) : normalizedContent;

        ChatAttachment attachment = new ChatAttachment();
        attachment.setFileName(fileName);
        attachment.setFileType(extension);
        attachment.setContentCategory(resolveContentCategory(extension));
        attachment.setContent(finalContent);
        attachment.setContentLength(normalizedContent.length());
        attachment.setTruncated(truncated);
        return attachment;
    }

    private void validateExtension(String extension) {
        if (StringUtils.isBlank(extension) || !SUPPORTED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("ai.attachment.unsupportedFileExtensions");
        }
    }

    private String resolveContentCategory(String extension) {
        return TABULAR_EXTENSIONS.contains(extension) ? "TABULAR" : "DOCUMENT";
    }

    private String parsePdf(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            return new PDFTextStripper().getText(document);
        }
    }

    private String parseDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    private String parseDoc(InputStream inputStream) throws IOException {
        try (HWPFDocument document = new HWPFDocument(inputStream);
             WordExtractor extractor = new WordExtractor(document)) {
            return extractor.getText();
        }
    }

    private String parseCsv(InputStream inputStream) throws IOException {
        List<String> lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder(2048);
        builder.append("[CSV]\n");
        int limit = Math.min(lines.size(), MAX_SHEET_ROWS + 1);
        for (int i = 0; i < limit; i++) {
            builder.append(lines.get(i)).append("\n");
        }
        if (lines.size() > limit) {
            builder.append("... omitted ").append(lines.size() - limit).append(" more rows");
        }
        return builder.toString();
    }

    private String parseWorkbook(InputStream inputStream) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            DataFormatter formatter = new DataFormatter();
            StringBuilder builder = new StringBuilder(4096);
            int numberOfSheets = workbook.getNumberOfSheets();
            for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                if (sheet == null) {
                    continue;
                }
                builder.append("[Sheet] ").append(sheet.getSheetName()).append("\n");
                int lastRowNum = Math.min(sheet.getLastRowNum(), MAX_SHEET_ROWS - 1);
                for (int rowIndex = 0; rowIndex <= lastRowNum; rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null) {
                        continue;
                    }
                    builder.append("Row ").append(rowIndex + 1).append(": ");
                    int lastCellNum = row.getLastCellNum();
                    int columnLimit = Math.min(lastCellNum < 0 ? 0 : lastCellNum, MAX_SHEET_COLUMNS);
                    for (int cellIndex = 0; cellIndex < columnLimit; cellIndex++) {
                        if (cellIndex > 0) {
                            builder.append(" | ");
                        }
                        Cell cell = row.getCell(cellIndex);
                        builder.append(readCellValue(formatter, cell));
                    }
                    if (lastCellNum > MAX_SHEET_COLUMNS) {
                        builder.append(" | ...");
                    }
                    builder.append("\n");
                }
                if (sheet.getLastRowNum() + 1 > MAX_SHEET_ROWS) {
                    builder.append("... omitted ")
                            .append(sheet.getLastRowNum() + 1 - MAX_SHEET_ROWS)
                            .append(" more rows\n");
                }
                builder.append("\n");
            }
            return builder.toString();
        } catch (Exception e) {
            throw new IOException("parse workbook failed", e);
        }
    }

    private String readCellValue(DataFormatter formatter, Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.FORMULA) {
            return formatter.formatCellValue(cell, cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator());
        }
        return formatter.formatCellValue(cell);
    }

    private String readPlainText(InputStream inputStream) throws IOException {
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }

    private String normalizeText(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }
        return content
                .replace("\u0000", "")
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .replaceAll("[\\t\\x0B\\f]+", " ")
                .replaceAll("\n{3,}", "\n\n")
                .trim();
    }
}
