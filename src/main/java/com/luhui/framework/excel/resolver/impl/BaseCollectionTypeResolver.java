package com.luhui.framework.excel.resolver.impl;

import com.google.common.collect.Lists;
import com.luhui.exception.ExcelParseException;
import com.luhui.framework.excel.ExcelFieldMapping;
import com.luhui.framework.excel.resolver.CreateInstance;
import com.luhui.framework.excel.resolver.ResolverContext;
import com.luhui.framework.excel.resolver.ResolverRegister;
import com.luhui.framework.util.GenericUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

/**
 * <p> </p>
 *
 * <pre> Created: 2019/3/18 13:58 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
@Slf4j
public abstract class BaseCollectionTypeResolver<T extends Collection> extends BaseObjTypeResolver implements CreateInstance<T> {

    @Override
    protected List<Object> doResolve(ResolverContext resolverContext) throws Exception {
        final ExcelFieldMapping excelFieldMapping = resolverContext.getExcelFieldMapping();
        log.info("集合类型解析字段:field={},value={},行数据={}",excelFieldMapping.getFieldName(),excelFieldMapping.getValue(),resolverContext.getRealCalcRowData());
        Collection collection = (Collection) PropertyUtils.getProperty(resolverContext.getEntity(),excelFieldMapping.getFieldName());
        if(collection==null){
            final Field field = FieldUtils.getField(resolverContext.getEntity().getClass(), excelFieldMapping.getFieldName(), true);
            collection = (Collection) ResolverRegister.getInstance().getCreateInstance(field.getType()).newInstance(field.getType());
            PropertyUtils.setProperty(resolverContext.getEntity(),excelFieldMapping.getFieldName(),collection);
        }
        final Field field = FieldUtils.getField(resolverContext.getEntity().getClass(),
                excelFieldMapping.getFieldName(), true);
        final Class<?> genericClass = GenericUtils.getFieldGeneric(field, 0);
        Object object;
        if(genericClass.isInterface()){
            object = ResolverRegister.getInstance().getCreateInstance(genericClass).newInstance(genericClass);
        }else {
            object = ConstructorUtils.invokeConstructor(genericClass, null);
        }
        collection.add(object);
        //返回需要递归装配的对象
        return Lists.newArrayList(object);
    }

    /**
     * 创建一个List类型对象
     * @param clazz
     * @return
     */
    @Override
    public T newInstance(Class<T> clazz) throws Exception {
        if(clazz.isInterface()){
            //接口或类型，返回ArrayList
            return createDefaultImpl();
        }else if(Modifier.isAbstract(clazz.getModifiers())){
            //如果是实现了List接口的一个抽象类
            throw new ExcelParseException(null,"类型class="+clazz.getName()+"是一个抽象类，装配失败！");
        }else{
            //否则创建真实类型对象
            return ConstructorUtils.invokeConstructor(clazz,null);
        }
    }

    /**
     * 创建接口默认实现
     * @return  默认实现类
     */
    protected abstract T createDefaultImpl();
}
