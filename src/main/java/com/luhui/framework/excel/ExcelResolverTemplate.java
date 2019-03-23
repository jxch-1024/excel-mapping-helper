package com.luhui.framework.excel;

import com.google.common.collect.Lists;
import com.luhui.binding.ParamConverter;
import com.luhui.binding.converters.YamlConverter;
import com.luhui.exception.ExcelParseException;
import com.luhui.framework.excel.resolver.*;
import com.luhui.framework.util.ExcelUtils;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

/**
 * <p> excel解析器模板 </p>
 *
 * <pre> Created: 2019/3/16 17:09 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
public class ExcelResolverTemplate {

    private ResolverFactoryBuilder resolverFactoryBuilder = new ResolverFactoryBuilder();

    static{
        //注册日期类型转换器，使得映射的类对象的字段支持使用Date类型，如果后期还需要支持其他类型的字段，可以继续注册类型转换器
        ConvertUtils.register(new Converter() {
            @Override
            public Object convert(Class type, Object value) {
                return new DateTime(value).toDate();
            }
        }, Date.class);
    }

    /**
     * 解析excel
     * @param path          excel文件路径
     * @param objectMappings 对象映射
     * @return
     */
    public List<Object> parse(String path,ObjectMapping... objectMappings){
        try {
            //注册本次解析需要解析的模型
            final ClassMappingRegister classMappingRegister = register(objectMappings);
            final List<List<String>> excelData = ExcelUtils.getExcelSheetData(path, 0);
            return resolvers(classMappingRegister, excelData);
        }catch (Exception e){
            throw new ExcelParseException(path,"excel解析失败！！",e);
        }
    }

    /**
     * 解析excel
     * @param path      excel文件路径
     * @param yaml      yaml文件内容
     * @return
     */
    public List<Object> parse(String path,String yaml){
        ParamConverter converter = new YamlConverter();
        final List<ObjectMapping> objectMappings = converter.converter(yaml);
        return parse(path,objectMappings.toArray(new ObjectMapping[0]));
    }

    /**
     * 解析所有被注册的对象
     * @param classMappingRegister      类映射注解器
     * @param excelData                 excel文件数据
     */
    private List<Object> resolvers(ClassMappingRegister classMappingRegister, List<List<String>> excelData){
        List<Object> result = Lists.newArrayList();
        for (ObjectMapping objectMapping : classMappingRegister.getClassMappings()) {
            final Object object = resolverObject(objectMapping, excelData);
            result.add(object);
        }
        return result;
    }

    /**
     * 注册对象映射关系
     * @param objectMappings   对象映射
     * @return                 对象注册器
     */
    private ClassMappingRegister register(ObjectMapping[] objectMappings) {
        ClassMappingRegister classMappingRegister = new ClassMappingRegister();
        for (ObjectMapping objectMapping : objectMappings) {
            classMappingRegister.register(objectMapping);
        }
        return classMappingRegister;
    }


    /**
     * 根据objectMapping解析一个对象
     * @param objectMapping     对象映射
     * @return 本次要解析的对象
     */
    private Object resolverObject(ObjectMapping objectMapping,List<List<String>> excelData){
        Class clazz = objectMapping.getClazz();
        final Object obj;
        try {
            if(clazz.isInterface()){
                obj = ResolverRegister.getInstance().getCreateInstance(clazz).newInstance(clazz);
            }else {
                obj = ConstructorUtils.invokeConstructor(clazz, null);
            }
        } catch (Exception e) {
           throw new ExcelParseException(null,clazz+"初始化失败！");
        }
        for (List<String> data : excelData) {
            try {
                resolveRow(objectMapping.getExcelFieldMappings(), obj, excelData, data);
            }catch (ExcelParseException e){
                throw e;
            }catch (Exception e){
                throw new ExcelParseException(null,"excel行数据解析失败！",e);
            }
        }
        return obj;
    }

    /**
     * 解析某一行数据
     * @param excelFieldMappings        字段映射
     * @param entity                    实体
     * @param excelAllData              excel表所有数据
     * @param currentRowData            当选要解析的行数据
     */
    private void resolveRow(List<ExcelFieldMapping> excelFieldMappings,Object entity,List<List<String>> excelAllData,
                              List<String> currentRowData) throws Exception {
        if(excelFieldMappings!=null){
            for (ExcelFieldMapping excelFieldMapping : excelFieldMappings) {
                final TypeResolver typeResolver =
                        resolverFactoryBuilder.newFactoryInstance(excelFieldMapping).getTypeResolver(excelFieldMapping, entity);
                ResolverContext resolverContext = new ResolverContext();
                resolverContext.setCurrentRowData(currentRowData);
                resolverContext.setExcelFieldMapping(excelFieldMapping);
                resolverContext.setEntity(entity);
                resolverContext.setAllData(excelAllData);
                typeResolver.resolve(resolverContext);
            }
        }
    }


}
