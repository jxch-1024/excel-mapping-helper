package com.luhui.framework.excel.resolver;

/**
 * <p> </p>
 *
 * <pre> Created: 2019/3/16 16:29 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
public interface TypeResolverRegister {

    /**
     * 注射一个类的对象解析器
     * @param clazz     类型
     * @param typeResolver  该类型的对象解析器
     */
    void registerTypeResolver(Class clazz, TypeResolver typeResolver);

    /**
     * 返回该类的对象解析器
     * @param clazz 类型
     * @return  该类型的对象解析器
     */
     TypeResolver getTypeResolver(Class clazz);
}
