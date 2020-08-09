package com.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//指明修饰的注解的生存周期
//运行级别保留，编译后的class文件中存在，在jvm运行时保留，可以被反射调用。
@Retention(RetentionPolicy.RUNTIME)
//指明了修饰的这个注解的使用范围，即被描述的注解可以用在哪里。
//TYPE:类，接口或者枚举
@Target(ElementType.TYPE)
public @interface ComponentScan
{
    String value() default "";
}
