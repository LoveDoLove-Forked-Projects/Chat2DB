package ai.chat2db.community.tools.wrapper;


public interface IResult<T> extends ITraceable{


    boolean success();


    void success(boolean success);


    String errorCode();


    void errorCode(String errorCode);


    String errorMessage();


    void errorMessage(String errorMessage);


    void errorDetail(String errorDetail);


    String errorDetail();


    void solutionLink(String solutionLink);


    String solutionLink();
}
