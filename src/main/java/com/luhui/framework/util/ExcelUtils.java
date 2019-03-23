package com.luhui.framework.util;

import com.google.common.collect.Lists;
import com.luhui.exception.ExcelParseException;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.List;

/**
 * <p> excel工具类 </p>
 *
 * <pre> Created: 2019/3/15 13:29 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
public class ExcelUtils {

    /**
     * 解析excel指定sheet数据，将每一行的数据装配成一个二维数组
     * @param path          解析excel文件路径
     * @param sheetIndex    sheet索引
     * @return
     */
    public static List<List<String>> getExcelSheetData(String path,int sheetIndex){
        try(final Workbook workbook = WorkbookFactory.create(new File(path))) {
            final Sheet sheet = workbook.getSheetAt(sheetIndex);
            List<List<String>> data = Lists.newArrayList();
            for(int rowNum = 0; rowNum<=sheet.getLastRowNum();rowNum++){
                final Row row = sheet.getRow(rowNum);
                List<String> rowData = Lists.newArrayList();
                if(row!=null) {
                    for (int cellNum =0 ;cellNum <= row.getLastCellNum();cellNum++) {
                        final Cell cell = row.getCell(cellNum);
                        if(cell!=null) {
                            rowData.add(cell.toString());
                        }else{
                            rowData.add(null);
                        }
                    }
                }
                data.add(rowData);
            }
            return data;
        }catch (Exception e){
            throw new ExcelParseException(path,"excel解析失败",e);
        }
    }

}
