package ai.chat2db.community.tools.wrapper.result;
import ai.chat2db.community.tools.wrapper.IResult;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@AllArgsConstructor
public class DataResult<T> implements IResult<T> {


    private Boolean success;


    private String errorCode;


    private String errorMessage;


    private String errorDetail;


    private String solutionLink;


    private T data;


    private String traceId;

    public DataResult() {
        this.success = Boolean.TRUE;
    }

    private DataResult(T data) {
        this();
        this.data = data;
    }


    public static <T> DataResult<T> of(T data) {
        return new DataResult<>(data);
    }


    public static <T> DataResult<T> empty() {
        return new DataResult<>();
    }


    public static <T> DataResult<T> error(String errorCode, String errorMessage) {
        DataResult<T> result = new DataResult<>();
        result.errorCode = errorCode;
        result.errorMessage = errorMessage;
        result.success = false;
        return result;
    }


    public static boolean hasData(DataResult<?> dataResult) {
        return dataResult != null && dataResult.getSuccess() && dataResult.getData() != null;
    }


    public <R> DataResult<R> map(Function<T, R> mapper) {
        R returnData = hasData(this) ? mapper.apply(getData()) : null;
        DataResult<R> dataResult = new DataResult<>();
        dataResult.setSuccess(getSuccess());
        dataResult.setErrorCode(getErrorCode());
        dataResult.setErrorMessage(getErrorMessage());
        dataResult.setData(returnData);
        dataResult.setTraceId(getTraceId());
        return dataResult;
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
