package com.luhui.framework.excel.resolver;

/**
 * <p>excel行类型解析器 </p>
 *
 * <pre> Created: 2019/3/16 15:29 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
public interface TypeResolver {

    /**
     * 解析这行数据
     * @param resolverContext 解析内容
     * @throws  Exception 异常信息
     */
    void resolve(ResolverContext resolverContext) throws Exception;
}
