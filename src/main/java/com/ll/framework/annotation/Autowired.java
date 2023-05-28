package com.ll.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD) // 이 어노테이션은 메서드에 붙는거다.
@Retention(RetentionPolicy.RUNTIME) // ??
public @interface Autowired {
}
