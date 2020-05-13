package com.rubbertranslator.modules.system.proxy;

import com.rubbertranslator.modules.system.SystemConfiguration;
import com.rubbertranslator.modules.system.SystemResourceManager;
import com.rubbertranslator.utils.JsonUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

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
            String json = JsonUtil.serialize(extractOriginConfig(configurationProxy));
            // XXX: 考虑加载单线程池来写
//            Logger.getLogger(this.getClass().getName()).info("setting changed:"+json);
            FileUtils.writeStringToFile(new File(SystemResourceManager.configJsonPath), json, StandardCharsets.UTF_8);
            return ret;
        }
        return method.invoke(target, args);
    }

    /**
     * 由于采用了两级代理：静态+动态，所以在序列化时，需要去掉动态和静态代理的部分
     */
    private SystemConfiguration extractOriginConfig(SystemConfiguration configuration) {
        SystemConfiguration newConfiguration =  new SystemConfiguration();
        try{
            // 取回静态代理对象 动态->静态
            // xxx: 文本处理模块嵌套过深，等待重新设计 文本模块由于设计问题，单独处理。一次性去掉动静态代理
            // xxx: 动态->静态
            newConfiguration.setUiConfig(doExtract(configuration.getUiConfig()));
            newConfiguration.setTextInputConfig(doExtract(configuration.getTextInputConfig()));
            newConfiguration.setProcessFilterConfig(doExtract(configuration.getProcessFilterConfig()));
            newConfiguration.setTranslatorConfig(doExtract(configuration.getTranslatorConfig()));
            newConfiguration.setHistoryConfig(doExtract(configuration.getHistoryConfig()));
            newConfiguration.setAfterProcessorConfig(doExtract(configuration.getAfterProcessorConfig()));

            // xxx: 静态->原
            // 取回源对象 静态->原
            newConfiguration.setUiConfig(
                    newConfiguration.getUiConfig()
            );

            // textInput
            newConfiguration.setTextInputConfig(
                    ((TextInputConfigStaticProxy)newConfiguration.getTextInputConfig()).getTextInputConfig()
            );

            // 过滤器
            newConfiguration.setProcessFilterConfig(
                    ((ProcessFilterConfigStaticProxy) (newConfiguration.getProcessFilterConfig())).getProcessFilterConfig()
             );

            // 翻译
            newConfiguration.setTranslatorConfig(
                    ((TranslatorConfigStaticProxy) newConfiguration.getTranslatorConfig()).getTranslatorConfig()
            );
            // 历史
            newConfiguration.setHistoryConfig(
                    ((HistoryConfigStaticProxy) newConfiguration.getHistoryConfig()).getHistoryConfig()
            );
            // 后置处理
            newConfiguration.setAfterProcessorConfig(
                    ((AfterProcessorConfigStaticProxy) newConfiguration.getAfterProcessorConfig()).getAfterProcessorConfig()
            );

            // xxx: 单独处理文本模块
            //FIXME: 文本处理模块嵌套过深，等待重新设计
            SystemConfiguration.TextProcessConfig.TextPreProcessConfig textPreProcessConfig =
                    ((TextPreProcessConfigStaticProxy)doExtract(configuration.getTextProcessConfig().getTextPreProcessConfig())).getPreProcessConfig();

            SystemConfiguration.TextProcessConfig.TextPostProcessConfig.WordsReplacerConfig wordsReplacerConfig =
                    ((WordsReplacerConfigStaticProxy)doExtract(configuration.getTextProcessConfig().getTextPostProcessConfig().getWordsReplacerConfig())).getWordsReplacerConfig();
            SystemConfiguration.TextProcessConfig.TextPostProcessConfig textPostProcessConfig = new SystemConfiguration.TextProcessConfig.TextPostProcessConfig();
            textPostProcessConfig.setWordsReplacerConfig(wordsReplacerConfig);
            textPostProcessConfig.setOpenPostProcess(configuration.getTextProcessConfig().getTextPostProcessConfig().isOpenPostProcess());

            SystemConfiguration.TextProcessConfig textProcessConfig = new SystemConfiguration.TextProcessConfig();
            textProcessConfig.setTextPreProcessConfig(textPreProcessConfig);
            textProcessConfig.setTextPostProcessConfig(textPostProcessConfig);

            newConfiguration.setTextProcessConfig(textProcessConfig);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return newConfiguration;
    }

    private <T> T copyObject(T t) {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        Object obj = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(t);
            // bos.toByteArray()一定要在oos.write之后
            bis = new ByteArrayInputStream(bos.toByteArray());
            ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                ois.close();
                bis.close();

                oos.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return (T) obj;
    }


    /**
     * 取回静态代理对象
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    private <T> T doExtract(T obj) throws IllegalAccessException {
        try{
            // 通过反射获取
            Field h1 = obj.getClass().getDeclaredField("CGLIB$CALLBACK_0");
            h1.setAccessible(true);
            Object temp = h1.get(obj);
            Field h2 = temp.getClass().getDeclaredField("target");
            h2.setAccessible(true);
            return (T) h2.get(temp);
        }catch (NoSuchFieldException e){
            return obj;
        }
    }
}
