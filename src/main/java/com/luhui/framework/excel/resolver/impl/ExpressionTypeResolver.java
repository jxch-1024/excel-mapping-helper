package com.luhui.framework.excel.resolver.impl;


import com.luhui.framework.excel.ExcelFieldMapping;
import com.luhui.framework.excel.resolver.ResolverContext;
import com.luhui.framework.excel.resolver.ResolverRegister;
import com.luhui.framework.util.OgnlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * <p> 常量表达式我们都认为是一个表达式，如[1],[1].substring 等等 </p>
 *
 * <pre> Created: 2019/3/16 15:42 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
@Slf4j
public class ExpressionTypeResolver extends BaseTypeResolver {

    @Override
    public void doTypeResolve(ResolverContext resolverContext) throws Exception {
        final ExcelFieldMapping excelFieldMapping = resolverContext.getExcelFieldMapping();
        log.info("表达式解析字段:field={},value={},行数据={}",excelFieldMapping.getFieldName(),excelFieldMapping.getValue(),resolverContext.getRealCalcRowData());
        final Object value = OgnlUtils.eval((String) excelFieldMapping.getValue(), resolverContext.getRealCalcRowData());
        setValue(resolverContext, value);
    }

    /**
     * 设置值
     * @param resolverContext
     * @param value
     * @throws Exception
     */
    private void setValue(ResolverContext resolverContext, Object value) throws Exception {
        if(value!=null){
            //这有个坑，对应的值类型可能还会被一个集合包裹，比如List<String>，因此我们要先判断他是不是一个集合类型。
            if(isCollection(resolverContext)){
                addCollectionValue(resolverContext,value);
            }else {
                try {
                    BeanUtils.setProperty(resolverContext.getEntity(), resolverContext.getExcelFieldMapping().getFieldName(), value);
                } catch (Exception e) {
                    log.error("属性装配失败。",e);
                }
            }
        }
    }

    /**
     * 设置默认值
     * @param resolverContext  解析内容
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Override
    protected void setDefaultValue(ResolverContext resolverContext) throws Exception {
        //集合类型没有默认值
        if(!isCollection(resolverContext)){
            setValue(resolverContext,resolverContext.getExcelFieldMapping().getDefaultValue());
        }
    }

    /**
     * 向集中追加数值
     * @param resolverContext
     */
    private void addCollectionValue(ResolverContext resolverContext,Object value) throws Exception {
        final Object entity = resolverContext.getEntity();
        final String fieldName = resolverContext.getExcelFieldMapping().getFieldName();
        //判断集合是否为空，为空创建一个新的集合，否则在集合中追加数据
        Collection collection = (Collection) PropertyUtils.getProperty(entity, fieldName);
        if(collection==null){
            final Field field = FieldUtils.getField(entity.getClass(), fieldName,true);
            collection = (Collection) ResolverRegister.getInstance().getCreateInstance(field.getType()).newInstance(field.getType());
            PropertyUtils.setProperty(entity,fieldName,collection);
        }
        collection.add(value);
    }

    private boolean isCollection(ResolverContext resolverContext) {
        final Field field = FieldUtils.getField(resolverContext.getEntity().getClass(), resolverContext.getExcelFieldMapping().getFieldName(), true);
        return Collection.class.isAssignableFrom(field.getType());
    }
}
