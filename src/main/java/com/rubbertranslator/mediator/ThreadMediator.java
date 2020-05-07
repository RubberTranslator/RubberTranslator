package com.rubbertranslator.mediator;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/7 11:28
 */
public class ThreadMediator extends Mediator{
    // 饿汉式-单例模式 TODO:static无法使用泛型，后期想办法优化
    private static ThreadMediator mediator = new ThreadMediator();
    // TODO:增加关系映射表应该可以降低耦合, source thread <--> dest threads
    private Map<Module,MessageEntity<String>> messageEntityHashMap = new HashMap<>();

    private ThreadMediator() {
    }

    public static ThreadMediator getInstance(){
        return mediator;
    }

    @Override
    public boolean register(Module module, MessageEntity<String> entity) {
        if(!messageEntityHashMap.containsKey(module)){
            messageEntityHashMap.put(module,entity);
            return true;
        }
        return false;
    }

    @Override
    public void passMessage(Module source, Module dest, String msg) throws UnRegisterException {
        if(messageEntityHashMap.containsKey(source)){
            // 判定
            MessageEntity<String> toEntity = messageEntityHashMap.get(dest);
            if(toEntity == null) throw new UnRegisterException("目标线程未注册");
            toEntity.receiveMsg(msg);
        } else{
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,"线程未注册，但发送了消息");
            throw new UnRegisterException("源线程未注册，无法发送");
        }
    }
}
