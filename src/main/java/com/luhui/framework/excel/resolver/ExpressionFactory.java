package com.luhui.framework.excel.resolver;

import com.luhui.framework.excel.ExcelFieldMapping;
import com.luhui.framework.excel.resolver.impl.ExpressionTypeResolver;

/**
 * <p> </p>
 *
 * <pre> Created: 2019/3/16 16:14 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
public class ExpressionFactory implements TypeResolverFactory {

    private ExpressionTypeResolver expressionTypeResolver = new ExpressionTypeResolver();

    private static final ExpressionFactory INSTANCE = new ExpressionFactory();

    private ExpressionFactory(){}

    public static ExpressionFactory getInstance(){
        return INSTANCE;
    }

    @Override
    public TypeResolver getTypeResolver(ExcelFieldMapping excelFieldMapping,Object object) {
        return expressionTypeResolver;
    }
}
