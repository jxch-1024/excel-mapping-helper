package com.luhui.framework.excel.resolver;

import com.luhui.framework.excel.ExcelFieldMapping;
import lombok.Data;

import java.util.List;

/**
 * <p> </p>
 *
 * <pre> Created: 2019/3/16 15:30 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
@Data
public class ResolverContext {

   private ExcelFieldMapping excelFieldMapping;
   private Object entity;
   /**
    * 当前行数据
    */
   private List<String> currentRowData;
   /**
    * 所有行数据
    */
   private List<List<String>> allData;
   /**
    * 需要计算的真实的行数据，如果偏移行为0，则他等于currentRowData，反之，要加上他偏移的行数
    */
   private List<String> realCalcRowData;

}
