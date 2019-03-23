package com.luhui.exception;

/**
 * <p> </p>
 *
 * <pre> Created: 2019/3/15 13:33 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
public class ExcelParseException extends RuntimeException {

    private String excelPath;

    public ExcelParseException(String path,String msg){
        super(msg);
        this.excelPath = path;
    }

    public ExcelParseException(String path,String msg,Throwable t){
        super(msg,t);
        this.excelPath = path;
    }
}
