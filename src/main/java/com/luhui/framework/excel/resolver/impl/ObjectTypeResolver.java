package com.luhui.framework.excel.resolver.impl;

import com.google.common.collect.Lists;
import com.luhui.framework.excel.ExcelFieldMapping;
import com.luhui.framework.excel.resolver.ResolverContext;
import com.luhui.framework.excel.resolver.ResolverRegister;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * <p> 普通的对象类型解析器 </p>
 *
 * <pre> Created: 2019/3/16 16:04 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
@Slf4j
public class ObjectTypeResolver extends BaseObjTypeResolver {

    @Override
    protected List<Object> doResolve(ResolverContext resolverContext) throws Exception {
        if(resolverContext.getEntity().getClass()==Map.class){
            return Lists.newArrayList(resolverContext.getEntity());
        }
        final ExcelFieldMapping excelFieldMapping = resolverContext.getExcelFieldMapping();
        final Field field = FieldUtils.getField(resolverContext.getEntity().getClass(), excelFieldMapping.getFieldName(), true);
        final Class<?> type = field.getType();
        log.info("对象解析器解析类class={},field={},fieldType={}",resolverContext.getEntity().getClass()
                , excelFieldMapping.getFieldName(),type);
        //给指定属性赋值
        Object propertyValue = PropertyUtils.getProperty(resolverContext.getEntity(), excelFieldMapping.getFieldName());
        if(propertyValue == null) {
            final Object obj;
            if(type.isInterface()){
                obj = ResolverRegister.getInstance().getCreateInstance(type).newInstance(type);
            }else{
                obj = ConstructorUtils.invokeConstructor(type,null);
            }
            BeanUtils.setProperty(resolverContext.getEntity(), excelFieldMapping.getFieldName(), obj);
            propertyValue = obj;
        }
        return Lists.newArrayList(propertyValue);
    }
}
