package com.luhui.framework.excel.resolver;

import com.luhui.framework.excel.ExcelFieldMapping;

/**
 * <p> 类型解析器工厂接口 </p>
 *
 * <pre> Created: 2019/3/16 16:11 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
public interface TypeResolverFactory {

    /**
     * 获取该类型的解析器
     * @param excelFieldMapping 要解析的字段映射
     * @param entity 要解析的对象
     * @return 对象解析器
     */
    TypeResolver getTypeResolver(ExcelFieldMapping excelFieldMapping, Object entity);
}
