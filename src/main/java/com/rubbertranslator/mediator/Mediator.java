package com.rubbertranslator.mediator;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/7 11:14
 * 通信中介，用于通信双方解耦
 */

public abstract class Mediator{

    public abstract boolean register(Module module, MessageEntity<String> entity);

    /**
     * 消息传递
     * @param source 消息发送方
     * @param dest 消息接收
     * @param msg 传递什么消息
     * @exception UnRegisterException 如果消息发送方没有向中介注册，那么将抛出这个异常
     */
    public abstract void passMessage(Module source, Module dest,String msg) throws UnRegisterException;

}

