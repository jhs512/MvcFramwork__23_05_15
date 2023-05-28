package com.ll.framework;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@AllArgsConstructor
public class RouteInfo {
    @Getter
    private String path;
    private String actionPath;
    @Getter
    private Class controllerCls;
    @Getter
    private Method method;
}