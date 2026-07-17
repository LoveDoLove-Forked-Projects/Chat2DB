package ai.chat2db.community.tools.wrapper.result;

import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * List response that preserves the existing array data contract and adds an exact total.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListResultWithTotal<T> extends ListResult<T> {

    private Long total;

    public ListResultWithTotal() {
        super();
    }

    private ListResultWithTotal(List<T> data, Long total) {
        this();
        List<T> safeData = data == null ? Collections.emptyList() : data;
        setData(safeData);
        this.total = total;
    }

    public static <T> ListResultWithTotal<T> from(List<T> data) {
        List<T> safeData = data == null ? Collections.emptyList() : data;
        return new ListResultWithTotal<>(safeData, (long) safeData.size());
    }

    public static <T> ListResultWithTotal<T> from(List<T> data, Long total) {
        return new ListResultWithTotal<>(data, total);
    }
}
