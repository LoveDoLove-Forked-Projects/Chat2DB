package ai.chat2db.community.tools.wrapper.result;
import ai.chat2db.community.tools.wrapper.IResult;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
@Data
@SuperBuilder
@AllArgsConstructor
public class ListResult<T> implements IResult<T> {
    private Boolean success;
    private String errorCode;
    private String errorMessage;
    private List<T> data;
    private String traceId;
    private String errorDetail;
    private String solutionLink;
    public ListResult() {
        this.success = Boolean.TRUE;
    }
    private ListResult(List<T> data) {
        this();
        this.data = data;
    }
    public static <T> ListResult<T> of(List<T> data) {
        return new ListResult<>(data);
    }
    public static <T> ListResult<T> empty() {
        return of(Collections.emptyList());
    }
    public static <T> ListResult<T> error(String errorCode, String errorMessage) {
        ListResult<T> result = new ListResult<>();
        result.errorCode = errorCode;
        result.errorMessage = errorMessage;
        result.success = Boolean.FALSE;
        return result;
    }
    public static boolean hasData(ListResult<?> listResult) {
        return listResult != null && listResult.getSuccess() && listResult.getData() != null && !listResult.getData()
            .isEmpty();
    }
    public <R> ListResult<R> map(Function<T, R> mapper) {
        List<R> returnData = hasData(this) ? getData().stream().map(mapper).collect(Collectors.toList())
            : Collections.emptyList();
        ListResult<R> listResult = new ListResult<>();
        listResult.setSuccess(getSuccess());
        listResult.setErrorCode(getErrorCode());
        listResult.setErrorMessage(getErrorMessage());
        listResult.setData(returnData);
        listResult.setTraceId(getTraceId());
        return listResult;
    }
    @Override
    public boolean success() {
        return success;
    }
    @Override
    public void success(boolean success) {
        this.success = success;
    }
    @Override
    public String errorCode() {
        return errorCode;
    }
    @Override
    public void errorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    @Override
    public String errorMessage() {
        return errorMessage;
    }
    @Override
    public void errorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    @Override
    public void errorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }
    @Override
    public String errorDetail() {
        return errorDetail;
    }
    @Override
    public void solutionLink(String solutionLink) {
        this.solutionLink = solutionLink;
    }
    @Override
    public String solutionLink() {
        return solutionLink;
    }
}
