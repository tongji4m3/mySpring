package com.tongji.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor
{
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
    {
        System.out.println("初始化前----MyBeanPostProcessor");
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
    {
        System.out.println("初始化后----MyBeanPostProcessor");
        return null;
    }
}
