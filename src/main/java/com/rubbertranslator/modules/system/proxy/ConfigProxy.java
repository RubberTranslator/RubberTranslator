package com.rubbertranslator.modules.system.proxy;

import com.rubbertranslator.modules.system.SystemConfiguration;
import com.rubbertranslator.modules.system.SystemResourceManager;
import com.rubbertranslator.utils.JsonUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

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
            SystemConfiguration configurationProxy = SystemResourceManager.getConfigurationProxy();
            String json = JsonUtil.serialize(extractOriginConfig(configurationProxy));
            // XXX: 考虑加载单线程池来写
//            Logger.getLogger(this.getClass().getName()).info("setting changed:"+json);
            FileUtils.writeStringToFile(new File(SystemResourceManager.configJsonPath),json, StandardCharsets.UTF_8);
            return ret;
        }
        return method.invoke(target, args);
    }

    /**
     * 由于采用了两级代理：静态+动态，所以在序列化时，需要去掉静态代理的部分
     */
    private SystemConfiguration extractOriginConfig(SystemConfiguration configuration){
        SystemConfiguration newConfiguration = new SystemConfiguration();
            try {
                 newConfiguration.setUiConfig((SystemConfiguration.UIConfig) doExtract(configuration.getUiConfig()));

                 newConfiguration.setTextInputConfig(
                         ((TextInputConfigStaticProxy) doExtract(configuration.getTextInputConfig())).getTextInputConfig()
                 );

                 newConfiguration.setProcessFilterConfig(
                         ((ProcessFilterStaticConfig) doExtract(configuration.getProcessFilterConfig())).getProcessFilterConfig()
                 );

                 SystemConfiguration.TextProcessConfig textProcessConfig =  new SystemConfiguration.TextProcessConfig();
                 textProcessConfig.setTextPreProcessConfig(
                         ((TextPreProcessStaticConfig) doExtract(configuration.getTextProcessConfig().getTextPreProcessConfig())).getPreProcessConfig()
                 );
                 textProcessConfig.setTextPostProcessConfig(
                         ((TextPostProcessStaticConfig) doExtract(configuration.getTextProcessConfig().getTextPostProcessConfig())).getTextPostProcessConfig()
                 );
                 newConfiguration.setTextProcessConfig(textProcessConfig);

                 newConfiguration.setTranslatorConfig(
                         ((TranslatorStaticConfig) doExtract(configuration.getTranslatorConfig())).getTranslatorConfig()
                 );
                 newConfiguration.setHistoryConfig(
                         ((HistoryStaticConfig) doExtract(configuration.getHistoryConfig())).getHistoryConfig()
                 );

                 newConfiguration.setAfterProcessorConfig(
                         ((AfterProcessorStaticConfig)doExtract(configuration.getAfterProcessorConfig())).getAfterProcessorConfig()
                 );
            } catch (Exception e) {
                e.printStackTrace();
            }
        return newConfiguration;
    }

    private Object doExtract(Object obj) throws NoSuchFieldException, IllegalAccessException {
        // 通过反射获取
        Field h1 = obj.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h1.setAccessible(true);
        Object temp = h1.get(obj);
        Field h2 = temp.getClass().getDeclaredField("target");
        h2.setAccessible(true);
        return h2.get(temp);
    }
}
