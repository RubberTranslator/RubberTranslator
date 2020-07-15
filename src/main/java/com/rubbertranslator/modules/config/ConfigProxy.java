package com.rubbertranslator.modules.config;

import com.rubbertranslator.modules.config.SystemConfiguration;
import com.rubbertranslator.modules.config.SystemConfigurationStaticProxy;
import com.rubbertranslator.system.SystemResourceManager;
import com.rubbertranslator.utils.FileUtil;
import com.rubbertranslator.utils.JsonUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/11 13:50
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
            SystemConfiguration configurationProxy = SystemResourceManager.getConfigurationProxy();
            String json = JsonUtil.serialize(configurationProxy.getConfiguration());
            // 使用后台线程来写入
            SystemResourceManager.getExecutor().execute(()->{
                try {
                    FileUtil.writeStringToFile(new File(SystemResourceManager.configJsonPath), json, StandardCharsets.UTF_8);
                    Logger.getLogger(this.getClass().getName()).info("更新设置:"+json);
                } catch (IOException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"更新设置时出错",e);
                }
            });
            return ret;
        }
        return method.invoke(target, args);
    }
}
