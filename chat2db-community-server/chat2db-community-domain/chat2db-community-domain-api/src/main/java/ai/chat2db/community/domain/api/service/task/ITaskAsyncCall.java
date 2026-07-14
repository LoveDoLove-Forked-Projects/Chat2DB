package ai.chat2db.community.domain.api.service.task;

import java.util.Map;


public interface ITaskAsyncCall {

    /**
     * Receives asynchronous progress or result attributes from a background task.
     *
     * @param map update attributes keyed by the producer contract.
     */
    void update(Map<String,Object> map);

}
