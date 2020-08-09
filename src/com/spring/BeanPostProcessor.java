package com.spring;

public interface BeanPostProcessor
{
    //实例化前后做的事情
    Object postProcessBeforeInitialization(Object bean, String beanName);
    Object postProcessAfterInitialization(Object bean, String beanName);
}
