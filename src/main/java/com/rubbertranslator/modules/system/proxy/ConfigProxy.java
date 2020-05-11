package com.rubbertranslator.modules.system.proxy;

import com.rubbertranslator.modules.system.SystemResourceManager;
import com.rubbertranslator.utils.JsonUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/11 13:50
 */
public class ConfigProxy implements MethodInterceptor {

    private Object target;

    public ConfigProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (method.getName().startsWith("set")) {
            Object ret = method.invoke(target, args);
            String json = JsonUtil.serialize(target);
            // TODO: 考虑加载单线程池来写
            Files.writeString(Paths.get(SystemResourceManager.configJsonPath),json);
            return ret;
        }
        return method.invoke(target, args);
    }
}
