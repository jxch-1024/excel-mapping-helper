package com.luhui.binding.converters;

import com.google.common.collect.Lists;
import com.luhui.binding.ParamConverter;
import com.luhui.exception.ConverterException;
import com.luhui.framework.excel.ExcelFieldMapping;
import com.luhui.framework.excel.ObjectMapping;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

/**
 * <p> yaml格式转换器 </p>
 *
 * <pre> Created: 2019/3/19 14:37 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
public class YamlConverter implements ParamConverter {


    private final List<SetProperty> setProperties = Lists.newArrayList();

    public YamlConverter(){
        registerSetProperties();
    }

    private void registerSetProperties() {
        //cond
        setProperties.add(new SetProperty() {
            @Override
            public boolean apply(String value) {
                return StringUtils.startsWithIgnoreCase(value,PropertyNames.CONDITION_PREFIX);
            }

            @Override
            public void doSetProperty(ExcelFieldMapping excelFieldMapping, String value) {
                excelFieldMapping.setCondition(value);
            }
        });

        //default
        setProperties.add(new SetProperty() {
            @Override
            public boolean apply(String value) {
                return StringUtils.startsWithIgnoreCase(value,PropertyNames.DEFAULT_PREFIX);
            }

            @Override
            public void doSetProperty(ExcelFieldMapping excelFieldMapping, String value) {
                excelFieldMapping.setDefaultValue(value);
            }
        });

        //rowOffset
        setProperties.add(new SetProperty() {
            @Override
            public boolean apply(String value) {
                return StringUtils.startsWithIgnoreCase(value,PropertyNames.ROW_OFFSET_PREFIX);
            }

            @Override
            public void doSetProperty(ExcelFieldMapping excelFieldMapping, String value) {
                excelFieldMapping.setRowOffset(NumberUtils.toInt(value,0));
            }
        });
    }

    @Override
    public List<ObjectMapping> converter(String text) {
        final Map<String, Object> yamlMap = parse(text);
        //第外层是个excel的键
        final Map<String, Object> excelMap = (Map<String, Object>) yamlMap.get("excel");
        List<ObjectMapping> objectMappings = Lists.newArrayList();
        //此时所有的键是类的类名，值是对应的属性映射
        if(excelMap!=null) {
            for (Map.Entry<String, Object> clsMap : excelMap.entrySet()) {
                final String clsName = clsMap.getKey();
                final Map<String, Object> clsFieldMap = (Map<String, Object>) clsMap.getValue();
                ObjectMapping objectMapping = new ObjectMapping();
                try {
                    objectMapping.setClazz(ClassUtils.getClass(clsName));
                } catch (ClassNotFoundException e) {
                    throw new ConverterException("类:"+clsName+"加载失败！",e);
                }
                objectMapping.setExcelFieldMappings(converterFieldMapping(clsFieldMap));
                objectMappings.add(objectMapping);
            }
        }
        return objectMappings;
    }

    /**
     * 解析class的字段映射
     * @param clsFieldMap
     */
    private List<ExcelFieldMapping> converterFieldMapping(Map<String, Object> clsFieldMap) {
        List<ExcelFieldMapping> excelFieldMappings = Lists.newArrayList();
        for (Map.Entry<String, Object> entry : clsFieldMap.entrySet()) {
            //键为属性名，值对应的属性值，他有可能是一个字符串，也有可能仍然是一个Map，因为他仍然是一个对象
            final String fieldName = entry.getKey();
            final Object value = entry.getValue();
            if(value==null){
                continue;
            }
            if(value instanceof String){
                final ExcelFieldMapping excelFieldMapping = toFieldMapping((String) value);
                if(excelFieldMapping!=null) {
                    excelFieldMapping.setValueType(ExcelFieldMapping.EXPRESSION_VALUE_TYPE);
                    if(((String) value).contains("|")) {
                        excelFieldMapping.setValue(StringUtils.substringAfterLast((String) value, "|"));
                    }else{
                        excelFieldMapping.setValue(value);
                    }
                    excelFieldMapping.setFieldName(fieldName);
                    excelFieldMappings.add(excelFieldMapping);
                }
            }else if(value instanceof Map){
                Map<String,Object> subObjMap = (Map<String, Object>) value;
                final String entryCnd = (String) subObjMap.get("entryCnd");
                ExcelFieldMapping excelFieldMapping = toFieldMapping(entryCnd);
                if(excelFieldMapping==null) {
                    excelFieldMapping = new ExcelFieldMapping();
                }
                excelFieldMapping.setValueType(ExcelFieldMapping.OBJECT_VALUE_TYPE);
                final Object propertiesMap = subObjMap.get("properties");
                if (propertiesMap != null) {
                    excelFieldMapping.setValue(converterFieldMapping((Map<String, Object>) propertiesMap));
                }
                excelFieldMapping.setFieldName(fieldName);
                excelFieldMappings.add(excelFieldMapping);
            }else{
                final ExcelFieldMapping excelFieldMapping = toFieldMapping(value.toString());
                excelFieldMapping.setFieldName(fieldName);
                excelFieldMapping.setValueType(ExcelFieldMapping.EXPRESSION_VALUE_TYPE);
                if(value.toString().contains("|")) {
                    excelFieldMapping.setValue(StringUtils.substringAfterLast(value.toString(), "|"));
                }else {
                    excelFieldMapping.setValue(value.toString());
                }
                excelFieldMappings.add(excelFieldMapping);
            }
        }
        return excelFieldMappings;
    }

    /**
     * 解析yaml文件内容，为key,value键值对
     * @param text
     * @return
     */
    private Map<String,Object> parse(String text){
        Yaml yaml = new Yaml();
        return (Map<String, Object>) yaml.load(text);
    }

    /**
     * 根据属性值转化成一个ExcelFieldMapping对象
     * @param values 格式如：cond:[1].equals('面额')|rowOffset:1|[0]
     * @return
     */
    private ExcelFieldMapping toFieldMapping(String values){
        final String[] valueArray = StringUtils.split(values, "|");
        if(valueArray!=null){
            final ExcelFieldMapping excelFieldMapping = new ExcelFieldMapping();
            for (String value : valueArray) {
                //获取要装配的属性名及这个属性的值
                for (SetProperty setProperty : setProperties) {
                    if(setProperty.apply(value)){
                        setProperty.doSetProperty(excelFieldMapping,StringUtils.substringAfter(value,":"));
                        break;
                    }
                }
            }
            return excelFieldMapping;
        }else{
            return null;
        }
    }

}
