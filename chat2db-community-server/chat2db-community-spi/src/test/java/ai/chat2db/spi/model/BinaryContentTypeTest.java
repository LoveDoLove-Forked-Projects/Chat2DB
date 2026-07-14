package ai.chat2db.community.domain.api.model;

import ai.chat2db.community.domain.api.enums.value.BinaryContentTypeEnum;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BinaryContentTypeTest {

    @Test
    void detectsOfficeOpenXmlFamiliesInsideZipContainer() throws IOException {
        assertEquals(BinaryContentTypeEnum.DOCX, BinaryContentTypeEnum.detect(zipWithEntry("word/document.xml")));
        assertEquals(BinaryContentTypeEnum.XLSX, BinaryContentTypeEnum.detect(zipWithEntry("xl/workbook.xml")));
        assertEquals(BinaryContentTypeEnum.PPTX, BinaryContentTypeEnum.detect(zipWithEntry("ppt/presentation.xml")));
    }

    private static byte[] zipWithEntry(String entryName) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            zipOutputStream.putNextEntry(new ZipEntry(entryName));
            zipOutputStream.write("content".getBytes(StandardCharsets.UTF_8));
            zipOutputStream.closeEntry();
        }
        return outputStream.toByteArray();
    }
}
