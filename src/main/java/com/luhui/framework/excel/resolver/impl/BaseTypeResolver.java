package com.luhui.framework.excel.resolver.impl;

import com.luhui.exception.ExcelParseException;
import com.luhui.framework.excel.ExcelFieldMapping;
import com.luhui.framework.excel.resolver.ResolverContext;
import com.luhui.framework.excel.resolver.TypeResolver;
import com.luhui.framework.util.OgnlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * <p> </p>
 *
 * <pre> Created: 2019/3/18 9:25 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
@Slf4j
public abstract class BaseTypeResolver implements TypeResolver {
    @Override
    public void resolve(ResolverContext resolverContext) throws Exception {
        final ExcelFieldMapping excelFieldMapping = resolverContext.getExcelFieldMapping();
        if(excelFieldMapping==null){
            throw new ExcelParseException(null,"excelFieldMapping不能为空");
        }
        //计算偏移
        calcRealRowData(resolverContext);
        if(CollectionUtils.isNotEmpty(resolverContext.getRealCalcRowData())) {
            try {
                //表达式为空，默认为真。否则就进行表达式的计算。
                if (StringUtils.isEmpty(excelFieldMapping.getCondition()) || OgnlUtils.evalRetBool(excelFieldMapping.getCondition(), resolverContext.getRealCalcRowData())) {
                    //走到这里，说明该字段至少已经匹配上一次了，我们设置他赋值成功，对于最后没有匹配上的，我们要给出默认值。默认值我们只允许普通值类型，对于对象类型不支持使用默认值
                    excelFieldMapping.setSuccess(true);
                    doTypeResolve(resolverContext);
                }
            }catch (IndexOutOfBoundsException e){
                //忽略索引越界的错误
            }catch (NullPointerException e){
                //有时候当前单元格的内容为空，此时执行表达式会抛出空指针
            }
        }
        if(!excelFieldMapping.isSuccess()){
            setDefaultValue(resolverContext);
        }
    }

    /**
     * 进行类型具体解析
     * @param resolverContext  解析内容
     * @throws  Exception 异常信息
     */
    protected abstract void doTypeResolve(ResolverContext resolverContext) throws Exception;

    /**
     * 设置默认值方法
     * @param resolverContext  解析内容
     */
    protected abstract void setDefaultValue(ResolverContext resolverContext) throws Exception;

    /**
     * 计算真实行，加上偏移
     * @param resolverContext   解析信息
     */
    private void calcRealRowData(ResolverContext resolverContext) {
        final List<String> currentRowData = resolverContext.getCurrentRowData();
        final ExcelFieldMapping excelFieldMapping = resolverContext.getExcelFieldMapping();
        final int rowOffset = excelFieldMapping.getRowOffset();
        final List<List<String>> allData = resolverContext.getAllData();
        final int index = allData.indexOf(currentRowData);
        if(allData.size() > index + rowOffset & index+rowOffset>=0){
            resolverContext.setRealCalcRowData(allData.get(index+rowOffset));
            if(rowOffset!=0) {
                log.info("属性field={}偏移增加{}行", resolverContext.getExcelFieldMapping().getFieldName(), rowOffset);
            }
        }

    }
}
