package com.luhui.framework.excel.resolver;

import com.luhui.framework.excel.ExcelFieldMapping;

/**
 * <p> 解析器工厂构造器 </p>
 *
 * <pre> Created: 2019/3/16 16:22 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
public class ResolverFactoryBuilder {

    /**
     * 根据不同的字段映射类型创建不同的工厂。调用方无需关注是哪个工厂创建的实例
     * @param excelFieldMapping
     * @return
     */
    public TypeResolverFactory newFactoryInstance(ExcelFieldMapping excelFieldMapping){
        if(ExcelFieldMapping.EXPRESSION_VALUE_TYPE.equals(excelFieldMapping.getValueType())){
            return ExpressionFactory.getInstance();
        }else if(ExcelFieldMapping.OBJECT_VALUE_TYPE.equals(excelFieldMapping.getValueType())){
            return ResolverRegister.getInstance();
        }
        return null;
    }
}
