package com.luhui.framework.excel.resolver.impl;

import com.google.common.collect.Lists;
import com.luhui.exception.ExcelParseException;
import com.luhui.framework.excel.ExcelFieldMapping;
import com.luhui.framework.excel.resolver.CreateInstance;
import com.luhui.framework.excel.resolver.ResolverContext;
import com.luhui.framework.excel.resolver.ResolverRegister;
import com.luhui.framework.util.GenericUtils;
import com.luhui.framework.util.OgnlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p> Map对象解析器 </p>
 *
 * <pre> Created: 2019/3/16 15:50 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
@Slf4j
public class MapTypeResolver extends BaseObjTypeResolver implements CreateInstance<Map> {


    @Override
    protected List<Object> doResolve(ResolverContext resolverContext) throws Exception {
        final ExcelFieldMapping excelFieldMapping = resolverContext.getExcelFieldMapping();
        log.info("map类型解析字段:field={},value={},行数据={}",excelFieldMapping.getFieldName(),excelFieldMapping.getValue(),resolverContext.getRealCalcRowData());
        if(Map.class.isAssignableFrom(resolverContext.getEntity().getClass())){
            List<Object> result = Lists.newArrayList();
            final Map map = (Map) resolverContext.getEntity();
            final ExcelFieldMapping subObjFieldMapping = resolverContext.getExcelFieldMapping();
            setMapValue(resolverContext, excelFieldMapping, map, result, subObjFieldMapping);
            return result;
        }

        Map map = (Map) PropertyUtils.getProperty(resolverContext.getEntity(),excelFieldMapping.getFieldName());
        if(map==null){
            final Field field = FieldUtils.getField(resolverContext.getEntity().getClass(), excelFieldMapping.getFieldName(), true);
            map = (Map) ResolverRegister.getInstance().getCreateInstance(field.getType()).newInstance(field.getType());
            PropertyUtils.setProperty(resolverContext.getEntity(),excelFieldMapping.getFieldName(),map);
        }
        List<ExcelFieldMapping> subObjFieldMappings = (List<ExcelFieldMapping>) excelFieldMapping.getValue();
        resolverContext.setEntity(map);
        List<Object> result = Lists.newArrayList();
        for (ExcelFieldMapping subObjFieldMapping : subObjFieldMappings) {
            setMapValue(resolverContext, excelFieldMapping, map, result, subObjFieldMapping);
        }
        //返回需要递归装配的对象
        return result;
    }

    /**
     * 设置map属性
     * @param resolverContext
     * @param excelFieldMapping
     * @param map
     * @param result
     * @param subObjFieldMapping
     * @throws Exception
     */
    private void setMapValue(ResolverContext resolverContext, ExcelFieldMapping excelFieldMapping, Map map, List<Object> result, ExcelFieldMapping subObjFieldMapping) throws Exception {
        if (ExcelFieldMapping.EXPRESSION_VALUE_TYPE.equals(subObjFieldMapping.getValueType())) {
            map.put(excelFieldMapping.getFieldName(), OgnlUtils.eval((String) subObjFieldMapping.getValue(), resolverContext.getRealCalcRowData()));
        } else if (ExcelFieldMapping.OBJECT_VALUE_TYPE.equals(subObjFieldMapping.getValueType())) {
            final Field field = FieldUtils.getField(resolverContext.getEntity().getClass(), subObjFieldMapping.getFieldName(), true);
            Class<?> fieldClass;
            try {
                fieldClass = GenericUtils.getFieldGeneric(field, 1);
                if (fieldClass == Object.class) {
                    fieldClass = Map.class;
                }
            } catch (Exception e) {
                fieldClass = Map.class;
            }
            final Object instance = ResolverRegister.getInstance().getCreateInstance(fieldClass).newInstance(fieldClass);
            result.add(instance);
        }
    }


    @Override
    public Map newInstance(Class<Map> clazz) throws Exception {
        if(clazz.isInterface()){
            //接口创建一个默认实现
            return new HashMap(16);
        }else if(Modifier.isAbstract(clazz.getModifiers())){
            //如果是实现了接口的一个抽象类
            throw new ExcelParseException(null,"类型class="+clazz.getName()+"是一个抽象类，装配失败！");
        }else{
            //否则创建真实类型对象
            return ConstructorUtils.invokeConstructor(clazz,null);
        }
    }
}
