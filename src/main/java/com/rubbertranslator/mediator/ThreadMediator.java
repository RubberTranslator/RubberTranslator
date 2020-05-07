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
            switch (source){
                case TEXT_READ_MODEL_CLIPBOARD_LISTENER:
                    fromTextReadToFilter(dest,msg);
                    break;
                case FILTER_MODULE_ACTIVE_WINDOW_LISTENER:
                    fromFilterToTextFormatter(dest,msg);
                    break;
            }
        } else{
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,"线程未注册，但发送了消息");
            throw new UnRegisterException("源线程未注册，无法发送");
        }
    }
    //TODO: 每个方法都是找到目标entity，判空，接收，可考虑用动态代理来做
    private void fromTextReadToFilter(Module toModule,String msg) throws UnRegisterException {
        MessageEntity<String> toEntity = messageEntityHashMap.get(toModule);
        if(toEntity == null) throw new UnRegisterException("目标线程未注册");
        toEntity.receiveMsg(msg);
    }

    private void fromFilterToTextFormatter(Module toModule, String msg) throws UnRegisterException {
        if(toModule == null){  // 如果toEntity是null，那么代表拦截器拦截了
            Logger.getLogger(this.getClass().getName()).log(Level.INFO,"消息已拦截");
        }else{
            Logger.getLogger(this.getClass().getName()).log(Level.INFO,"接收"+msg);
//            toEntity.receiveMsg(msg);
            MessageEntity<String> toEntity = messageEntityHashMap.get(toModule);
            if(toEntity == null) throw new UnRegisterException("目标线程未注册");
            toEntity.receiveMsg(msg);
        }
    }
}
