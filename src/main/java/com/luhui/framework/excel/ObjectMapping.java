package com.luhui.framework.excel;

import lombok.Data;

import java.util.List;


/**
 * <p> </p>
 *
 * <pre> Created: 2019/3/15 16:26 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
@Data
public class ObjectMapping {

    /**
     * 映射的类名
     */
    private Class clazz;
    /**
     * 所有字段映射
     */
    private List<ExcelFieldMapping> excelFieldMappings;

}
