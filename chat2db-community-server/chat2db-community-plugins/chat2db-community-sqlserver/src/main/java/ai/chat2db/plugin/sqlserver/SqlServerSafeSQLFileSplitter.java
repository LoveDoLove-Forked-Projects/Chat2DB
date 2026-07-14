package ai.chat2db.plugin.sqlserver;

import ai.chat2db.spi.DefaultSQLFileSplitter;
import ai.chat2db.community.domain.api.enums.parser.FileSizeUnitEnum;

import java.io.File;
import java.nio.charset.Charset;

public class SqlServerSafeSQLFileSplitter extends DefaultSQLFileSplitter {


    public SqlServerSafeSQLFileSplitter(File file) {
        super(file);
        super.addBracketPairSymbol('[',']');

    }

    public SqlServerSafeSQLFileSplitter(long size, FileSizeUnitEnum unit, File file, Charset charSet) {
        super(size, unit, file, charSet);
        super.addBracketPairSymbol('[',']');
    }


}
