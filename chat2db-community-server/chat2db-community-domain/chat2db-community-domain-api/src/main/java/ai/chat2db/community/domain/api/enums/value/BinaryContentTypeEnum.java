package ai.chat2db.community.domain.api.enums.value;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public enum BinaryContentTypeEnum {
    PNG("image/png", ".png", true),
    JPEG("image/jpeg", ".jpg", true),
    GIF("image/gif", ".gif", true),
    WEBP("image/webp", ".webp", true),
    BMP("image/bmp", ".bmp", true),
    PDF("application/pdf", ".pdf", false),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx", false),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx", false),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", ".pptx", false),
    ZIP("application/zip", ".zip", false),
    UNKNOWN("application/octet-stream", ".bin", false);

    private final String contentType;
    private final String extension;
    private final boolean image;

    BinaryContentTypeEnum(String contentType, String extension, boolean image) {
        this.contentType = contentType;
        this.extension = extension;
        this.image = image;
    }

    public String contentType() {
        return contentType;
    }

    public String extension() {
        return extension;
    }

    public boolean isImage() {
        return image;
    }

    public static BinaryContentTypeEnum detect(byte[] sample) {
        if (sample == null || sample.length < 4) {
            return UNKNOWN;
        }
        if (startsWith(sample, 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a)) {
            return PNG;
        }
        if (startsWith(sample, 0xff, 0xd8)) {
            return JPEG;
        }
        if (startsWith(sample, 0x47, 0x49, 0x46, 0x38)) {
            return GIF;
        }
        if (startsWith(sample, 0x52, 0x49, 0x46, 0x46)
                && sample.length >= 12
                && sample[8] == 0x57
                && sample[9] == 0x45
                && sample[10] == 0x42
                && sample[11] == 0x50) {
            return WEBP;
        }
        if (startsWith(sample, 0x42, 0x4d)) {
            return BMP;
        }
        if (startsWith(sample, 0x25, 0x50, 0x44, 0x46)) {
            return PDF;
        }
        if (startsWith(sample, 0x50, 0x4b, 0x03, 0x04)
                || startsWith(sample, 0x50, 0x4b, 0x05, 0x06)
                || startsWith(sample, 0x50, 0x4b, 0x07, 0x08)) {
            return detectZipFamily(sample);
        }
        return UNKNOWN;
    }

    public static BinaryContentTypeEnum fromContentType(String contentType) {
        if (contentType == null) {
            return UNKNOWN;
        }
        String normalized = contentType.toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "image/png" -> PNG;
            case "image/jpeg" -> JPEG;
            case "image/gif" -> GIF;
            case "image/webp" -> WEBP;
            case "image/bmp" -> BMP;
            case "application/pdf" -> PDF;
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> DOCX;
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> XLSX;
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> PPTX;
            case "application/zip" -> ZIP;
            default -> UNKNOWN;
        };
    }

    private static boolean startsWith(byte[] sample, int... bytes) {
        if (sample.length < bytes.length) {
            return false;
        }
        for (int i = 0; i < bytes.length; i++) {
            if ((sample[i] & 0xff) != bytes[i]) {
                return false;
            }
        }
        return true;
    }

    private static BinaryContentTypeEnum detectZipFamily(byte[] sample) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(sample))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName().toLowerCase(Locale.ROOT);
                if (entryName.startsWith("word/")) {
                    return DOCX;
                }
                if (entryName.startsWith("xl/")) {
                    return XLSX;
                }
                if (entryName.startsWith("ppt/")) {
                    return PPTX;
                }
            }
        } catch (IOException ignored) {
            return ZIP;
        }
        return ZIP;
    }
}
