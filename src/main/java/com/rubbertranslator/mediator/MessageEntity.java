package com.rubbertranslator.mediator;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/7 11:24
 */
public interface MessageEntity<M> {
    void receiveMsg(M msg);

    void sendMsg(M msg);
}
