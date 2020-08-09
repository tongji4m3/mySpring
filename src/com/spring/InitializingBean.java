package com.spring;

//bean生成过程中,调用该接口进行初始化
public interface InitializingBean
{
    void afterPropertiesSet();
}
