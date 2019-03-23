package com.luhui.framework.excel.resolver;

/**
 * <p> </p>
 *
 * <pre> Created: 2019/3/18 14:53 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
public interface CreateInstanceRegister {

    /**
     * 注册一个创建实例的注册器
     * @param clazz     类型
     * @param createInstance  该类型的对象解析器
     */
    void registerCreateInstance(Class clazz, CreateInstance createInstance);

    /**
     * 返回该类的对象解析器
     * @param clazz 类型
     * @return  该类型的对象解析器
     */
    CreateInstance getCreateInstance(Class clazz);
}
