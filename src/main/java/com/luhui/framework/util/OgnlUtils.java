package com.luhui.framework.util;


import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

/**
 * <p> </p>
 *
 * <pre> Created: 2019/3/15 11:19 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
public class OgnlUtils {

    /**
     * 执行一个eval表达式，无参数
     * @param expression 表达式
     */
    public static <T> T eval(String expression) throws OgnlException {
        Object value = Ognl.getValue(expression, (Object) null,null);
        return (T) value;
    }

    /**
     * 执行一个eval表达式，带参数
     * @param expression
     * @param param
     * @param <T>
     * @return
     */
    public static <T> T eval(String expression,Object param) throws OgnlException {
        OgnlContext context = new OgnlContext();
        context.put("param", param);
        context.setRoot(param);
        Object value = Ognl.getValue(expression, context,context.getRoot());
        return (T) value;
    }

    /**
     * 计算一个表达式，并且返回值必须是布尔类型
     * @param expression
     * @param param
     * @return
     * @throws OgnlException
     */
    public static boolean evalRetBool(String expression, Object param) throws OgnlException {
        final Object result = eval(expression, param);
        if(result !=null && result instanceof Boolean){
            return (boolean) result;
        }else{
            throw new RuntimeException("表达式:"+expression+"返回值必须是布尔类型！");
        }
    }
}
