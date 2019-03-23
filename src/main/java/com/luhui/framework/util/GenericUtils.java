package com.luhui.framework.util;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class GenericUtils {

    /**
     * 反射获取字段的泛型的真实类型
     * @param field
     * @param index
     * @return
     */
    public static Class<?> getFieldGeneric(Field field, int index){
        final Type genericType = field.getGenericType();
        ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) genericType;
        final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return (Class<?>) actualTypeArguments[index];
    }
}
