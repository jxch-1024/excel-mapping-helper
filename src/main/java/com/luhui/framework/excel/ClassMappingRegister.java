package com.luhui.framework.excel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <p> </p>
 *
 * <pre> Created: 2019/3/15 15:09 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
public class ClassMappingRegister {

    private final Map<Class,ObjectMapping> clazzMappings = new HashMap<>();


    /**
     * 注册类型转换器
     * @param objectMapping
     */
    public void register(ObjectMapping objectMapping){
        clazzMappings.put(objectMapping.getClazz(),objectMapping);
    }



    public Collection<ObjectMapping> getClassMappings() {
        return clazzMappings.values();
    }
}
