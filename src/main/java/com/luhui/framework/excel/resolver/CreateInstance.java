package com.luhui.framework.excel.resolver;

/**
 * <p> </p>
 *
 * <pre> Created: 2019/3/18 14:46 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
public interface CreateInstance<T> {

    /**
     * 创建一个对象
     * @param clazz
     * @throws Exception
     * @return
     */
    T newInstance(Class<T> clazz) throws Exception;
}
