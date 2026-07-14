package ai.chat2db.community.jcef.listener;


import ai.chat2db.community.jcef.event.IEvent;
public interface IEventListener {
    void onEvent(IEvent event);
}
