package com.luhui.binding;

import com.luhui.framework.excel.ExcelFieldMapping;
import com.luhui.framework.excel.ObjectMapping;

import java.util.List;

/**
 * <p> 参数转换器 </p>
 *
 * <pre> Created: 2019/3/19 14:34 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
public interface ParamConverter {

    /**
     * 根据指定的文本信息转换成若干个对象映射
     * @param text  文本信息
     * @return      对象映射体
     */
    List<ObjectMapping> converter(String text);


    interface PropertyNames{

        String CONDITION_PREFIX = "cond:";

        String DEFAULT_PREFIX = "default:";

        String ROW_OFFSET_PREFIX = "rowOffset:";
    }

    interface SetProperty{

        /**
         * 是否为这个属性赋值
         * @param value 属性值，属性值包含前缀，如cond:[1].equals('xxx')
         * @return  是否调用doSetProperty方法
         */
        boolean apply(String value);

        /**
         * 设置属性值
         * @param excelFieldMapping excel映射类
         * @param value 属性值，不包含前缀
         */
        void doSetProperty(ExcelFieldMapping excelFieldMapping, String value);
    }
}
