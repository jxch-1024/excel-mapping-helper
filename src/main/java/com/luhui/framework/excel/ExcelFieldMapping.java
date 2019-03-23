package com.luhui.framework.excel;

import lombok.Data;


/**
 * <p> </p>
 *
 * <pre> Created: 2019/3/15 14:41 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
@Data
public class ExcelFieldMapping {

    public static final String EXPRESSION_VALUE_TYPE = "expression";
    public static final String OBJECT_VALUE_TYPE = "object";

    /**
     * 属性名
     */
    private String fieldName;
    /**
     * 如果是普通值或者是一个表达式，我们都认为他是一个表达式，因为普通值也是一个表达式，就是一个String。
     * 如果是一个对象，则是一个List<ExcelFieldMapping>
     */
    private Object value;
    /**
     * 值的类型，expression/object
     */
    private String valueType;
    /**
     * 表达式
     */
    private String condition;
    /**
     * 行偏移数
     */
    private int rowOffset;
    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 如果为true,说明匹配成功，false没有匹配成功，会赋值默认值
     */
    private boolean success;


}
