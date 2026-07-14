package ai.chat2db.community.tools.handler;


public interface IEasyCallBackHandler {


    default void preHandle() {
    }


    default void postHandle() {
    }


    default void afterCompletion() {
    }
}
