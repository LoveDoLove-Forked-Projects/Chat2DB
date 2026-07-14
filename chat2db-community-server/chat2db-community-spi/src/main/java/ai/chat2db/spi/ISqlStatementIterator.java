


package ai.chat2db.spi;

import ai.chat2db.spi.util.SplitSqlString;
import java.util.Iterator;

public interface ISqlStatementIterator extends Iterator<SplitSqlString> {


    long iteratedBytes();

}
