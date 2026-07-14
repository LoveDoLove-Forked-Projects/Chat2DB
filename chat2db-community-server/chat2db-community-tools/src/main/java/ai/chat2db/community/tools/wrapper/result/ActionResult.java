package ai.chat2db.community.tools.wrapper.result;
import ai.chat2db.community.tools.wrapper.IResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
@Data
@SuperBuilder
@AllArgsConstructor
public class ActionResult implements IResult {
    private Boolean success;
    private String errorCode;
    private String errorMessage;
    private String errorDetail;
    private String solutionLink;
    private String traceId;
    public ActionResult() {
        this.success = Boolean.TRUE;
    }
    public static ActionResult isSuccess() {
        return new ActionResult();
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
    public static ActionResult fail(String errorCode, String errorMessage, String errorDetail) {
        ActionResult result = new ActionResult();
        result.errorCode = errorCode;
        result.errorMessage = errorMessage;
        result.success = Boolean.FALSE;
        result.solutionLink("https://github.com/chat2db/Chat2DB/wiki/Chat2DB");
        result.errorDetail(errorDetail);
        return result;
    }
    public DataResult<Boolean> toBooleaSuccessnDataResult() {
        return DataResult.<Boolean>builder()
            .success(success)
            .errorCode(errorCode)
            .errorMessage(errorMessage)
            .errorDetail(errorDetail)
            .solutionLink(solutionLink)
            .traceId(traceId)
            .data(Boolean.TRUE)
            .build();
    }
}
