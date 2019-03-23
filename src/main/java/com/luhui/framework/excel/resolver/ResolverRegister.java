package com.luhui.framework.excel.resolver;

import com.google.common.collect.Maps;
import com.luhui.exception.ExcelParseException;
import com.luhui.framework.excel.ExcelFieldMapping;
import com.luhui.framework.excel.resolver.impl.ListTypeResolver;
import com.luhui.framework.excel.resolver.impl.MapTypeResolver;
import com.luhui.framework.excel.resolver.impl.ObjectTypeResolver;
import com.luhui.framework.excel.resolver.impl.SetTypeResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p> 对象的的类型解析器注册器，同样是对象，可能有些对象我们要单独解析，比如List,Set,Map，我们只需要把这三个解析器注册进这个容器中，
 * 在我们解析对应对象的时候，如果找不到对应的解析器，就会使用普通的对象解析器，否则就会调用特殊宝岛的对象解析器
 * </p>
 *
 * <pre> Created: 2019/3/16 15:44 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
@Slf4j
public class ResolverRegister implements TypeResolverRegister,TypeResolverFactory,CreateInstanceRegister {

    /**
     * 类，对象解析器的键值对
     */
    private final Map<Class,TypeResolver> resolvers = Maps.newConcurrentMap();

    /**
     * 创建实例的键值对
     */
    private final Map<Class,CreateInstance> createInstances = Maps.newConcurrentMap();

    /**
     * 默认的对象解析器
     */
    private final TypeResolver DEFAULT_TYPE_RESOLVER = new ObjectTypeResolver();

    private static final ResolverRegister INSTANCE = new ResolverRegister();

    private ResolverRegister(){
        final ListTypeResolver listTypeResolver = new ListTypeResolver();
        registerTypeResolver(List.class,listTypeResolver);
        log.info("注册List解析器成功。");
        registerCreateInstance(List.class,listTypeResolver);
        log.info("注册List类创建器成功。");

        final MapTypeResolver mapTypeResolver = new MapTypeResolver();
        registerTypeResolver(Map.class,mapTypeResolver);
        log.info("注册Map解析器成功。");
        registerCreateInstance(Map.class,mapTypeResolver);
        log.info("注册Map类创建器成功。");

        final SetTypeResolver setTypeResolver = new SetTypeResolver();
        registerTypeResolver(Set.class,setTypeResolver);
        log.info("注册Set解析器成功。");
        registerCreateInstance(Set.class,setTypeResolver);
        log.info("注册Map类创建器成功。");
    }

    /**
     * 单例模式，获取该类对象
     * @return
     */
    public static ResolverRegister getInstance(){
        return INSTANCE;
    }
    /**
     * 注射一个类的对象解析器
     * @param clazz     类型
     * @param typeResolver  该类型的对象解析器
     */
    @Override
    public void registerTypeResolver(Class clazz, TypeResolver typeResolver){
        if(clazz.isPrimitive()){
            throw new ExcelParseException(null,clazz+"不是一个对象类型！");
        }
        resolvers.put(clazz,typeResolver);
    }


    /**
     * 返回该类的对象解析器
     * @param clazz
     * @return
     */
    @Override
    public TypeResolver getTypeResolver(Class clazz){
        TypeResolver typeResolver = resolvers.get(clazz);
        if(typeResolver!=null){
            log.debug("class={}查找到对应的解析器为:{}",clazz.getName(),typeResolver.getClass().getName());
            return typeResolver;
        }
        log.info("class={}查找到的对应的解析器为空，准备查询他的父类/接口的对象解析器。",clazz.getName());
        //如果获取到的对象解析器为空，寻找该clazz的父类、接口有没有被注册过，如果有，返回他父类或接口的对象解析器
        for (Class registerClazz : resolvers.keySet()) {
            if(registerClazz.isAssignableFrom(clazz)){
                typeResolver = resolvers.get(registerClazz);
                log.debug("class={}查找到父类/接口类型class={}的解析器为={}",clazz.getName(),
                        registerClazz.getName(),typeResolver.getClass().getName());
                //返回他的父类/接口注册的对象解析器
                return typeResolver;
            }
        }
        //所有条件都没有找到对象解析器，返回普通对象解析器
        log.debug("class={}没有找到任何类型解析器，返回默认的普通对象解析器。",clazz.getName());
        return DEFAULT_TYPE_RESOLVER;
    }

    @Override
    public TypeResolver getTypeResolver(ExcelFieldMapping excelFieldMapping,Object object) {
        log.debug("解析类型class={},field={}", object.getClass().getName(), excelFieldMapping.getFieldName());
        if(Map.class.isAssignableFrom(object.getClass())){
            return getTypeResolver(Map.class);
        }
        final Field field = FieldUtils.getField(object.getClass(), excelFieldMapping.getFieldName(), true);
        final Class<?> fieldType = field.getType();
        if (fieldType.isPrimitive()) {
            throw new ExcelParseException(null, "你要解析的类属性" + excelFieldMapping.getFieldName() + "不是一个对象类型！");
        }
        return getTypeResolver(fieldType);
    }


    @Override
    public void registerCreateInstance(Class clazz, CreateInstance createInstance) {
        createInstances.put(clazz,createInstance);
    }

    @Override
    public CreateInstance getCreateInstance(Class clazz) {
        CreateInstance createInstance = createInstances.get(clazz);
        if(createInstance!=null){
            log.debug("class={}查找到对应的类创建器为:{}",clazz.getName(),createInstance.getClass().getName());
            return createInstance;
        }
        log.info("class={}查找到的对应的类创建器为空，准备查询他的父类/接口的对象类创建器。",clazz.getName());
        //如果获取到的对象解析器为空，寻找该clazz的父类、接口有没有被注册过，如果有，返回他父类或接口的对象解析器
        for (Class registerClazz : createInstances.keySet()) {
            if(registerClazz.isAssignableFrom(clazz)){
                createInstance = createInstances.get(registerClazz);
                log.debug("class={}查找到父类/接口类型class={}的类创建器为={}",clazz.getName(),
                        registerClazz.getName(),createInstance.getClass().getName());
                //返回他的父类/接口注册的对象解析器
                return createInstance;
            }
        }
        return null;
    }
}
