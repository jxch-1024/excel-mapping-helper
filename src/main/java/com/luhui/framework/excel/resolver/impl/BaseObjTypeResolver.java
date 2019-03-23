package com.luhui.framework.excel.resolver.impl;

import com.luhui.framework.excel.ExcelFieldMapping;
import com.luhui.framework.excel.resolver.ResolverContext;
import com.luhui.framework.excel.resolver.ResolverFactoryBuilder;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * <p> 对象类型解析器父类 </p>
 *
 * <pre> Created: 2019/3/18 9:16 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
public abstract class BaseObjTypeResolver extends BaseTypeResolver {

    @Override
    public void doTypeResolve(ResolverContext resolverContext) throws Exception {
        final List<Object> objs = doResolve(resolverContext);
        if(CollectionUtils.isNotEmpty(objs)) {
            for (Object obj : objs) {
                resolveObject(resolverContext, obj);
            }
        }
    }

    /**
     * 具体解析内容
     * @param resolverContext  解析内容
     * @return 需要递归解析的对象
     * @throws  Exception  异常信息
     */
    protected abstract List<Object> doResolve(ResolverContext resolverContext) throws Exception;

    @Override
    protected void setDefaultValue(ResolverContext resolverContext) {
        //该情况下不需要设置默认值

    }


    protected void resolveObject(ResolverContext resolverContext, Object obj) throws Exception {
        final ExcelFieldMapping excelFieldMapping = resolverContext.getExcelFieldMapping();

        List<ExcelFieldMapping> subObjFieldMappings = (List<ExcelFieldMapping>) excelFieldMapping.getValue();
        //再获取对应的类型解析器去递归解析
        final ResolverFactoryBuilder resolverFactoryBuilder = new ResolverFactoryBuilder();
        resolverContext.setEntity(obj);
        resolverContext.setRealCalcRowData(null);
        for (ExcelFieldMapping subObjFieldMapping : subObjFieldMappings) {
            resolverContext.setExcelFieldMapping(subObjFieldMapping);
            resolverFactoryBuilder.newFactoryInstance(subObjFieldMapping)
                    .getTypeResolver(subObjFieldMapping,obj).resolve(resolverContext);
        }
    }
}
