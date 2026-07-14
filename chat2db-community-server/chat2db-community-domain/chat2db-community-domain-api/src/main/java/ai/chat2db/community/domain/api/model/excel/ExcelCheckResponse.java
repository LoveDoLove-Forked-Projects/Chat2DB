package ai.chat2db.community.domain.api.model.excel;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExcelCheckResponse {

    private List<Sheet> sheetList;

    private String filePath;

    @Data
    public static class Sheet {
        private Integer sheetNo;
        private String sheetName;
        private int rowNum;
        private int colNum;
        private List<Header> headerList;
        private List<List<Object>> dataList;
        private String ddl;
        private String tableName;
        private String tableType;
        private int headerStartRowNum;
        private int headerEndRowNum;
        private int headerStartColNum;
        private int headerEndColNum;
        private boolean del;

        public void addHeader(Header header) {
            if (headerList == null) {
                headerList = new java.util.ArrayList<>();
            }
            headerList.add(header);
        }

        public List<String> getHeaderNameList(){
           List<String>  headerNameList = new ArrayList<>();
            for (int i = 0; i < getHeaderList().size(); i++) {
                ExcelCheckResponse.Header header = getHeaderList().get(i);
                headerNameList.add("\"" + header.getHeaderName() + "\"");
            }
            return headerNameList;
        }
    }

    @Data
    public static class Header {
        private String headerName;
        private int colNum;
        private String dataType;
        private String comment;
    }
}
