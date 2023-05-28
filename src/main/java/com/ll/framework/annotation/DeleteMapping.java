package com.ll.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 이 어노테이션은 메서드에 붙는거다.
@Retention(RetentionPolicy.RUNTIME) // ??
public @interface DeleteMapping {
    String value(); // 어노테이션 달때, value(String) 입력가능하게 함
}
