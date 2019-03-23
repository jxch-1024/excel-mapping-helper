package com.luhui.framework.excel.resolver.impl;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * <p> List对象解析器 </p>
 *
 * <pre> Created: 2019/3/16 15:49 </pre>
 *
 * @author hlu
 * @version 1.0
 * @since JDK 1.7
 */
@Slf4j
public class ListTypeResolver extends BaseCollectionTypeResolver<List> {


    @Override
    protected List createDefaultImpl() {
        return new ArrayList();
    }
}
