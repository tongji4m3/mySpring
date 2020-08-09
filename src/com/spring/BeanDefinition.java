package com.spring;

public class BeanDefinition
{
    private String scope;//是否是单例的
    private Class beanClass;

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }

    public Class getBeanClass()
    {
        return beanClass;
    }

    public void setBeanClass(Class beanClass)
    {
        this.beanClass = beanClass;
    }

    @Override
    public String toString()
    {
        return "BeanDefinition{" +
                "scope='" + scope + '\'' +
                ", beanClass=" + beanClass +
                '}';
    }
}
