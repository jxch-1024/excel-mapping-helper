package com.luhui.framework.excel.resolver.impl;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;


/**
 * <p> Set对象解析器 </p>
 *
 * <pre> Created: 2019/3/16 15:50 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
@Slf4j
public class SetTypeResolver extends BaseCollectionTypeResolver<Set> {


    @Override
    protected Set createDefaultImpl() {
        return new HashSet();
    }
}
