package com.luhui.exception;

/**
 * <p> </p>
 *
 * <pre> Created: 2019/3/19 15:03 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
public class ConverterException extends RuntimeException {

    public ConverterException(String msg){
        super(msg);
    }

    public ConverterException(String msg,Throwable t){
        super(msg,t);
    }
}
