package com.tongji.service;

import com.spring.*;

@Component("orderService")
@Scope("prototype")
public class OrderService implements InitializingBean, BeanNameAware
{
    @Autowired
    private UserService userService;

    private String beanName;//spring把beanName自动初始化orderService

    public void test()
    {
        System.out.println(userService);
        System.out.println(beanName);
    }

    @Override
    public void afterPropertiesSet()
    {
        System.out.println("初始化");
    }

    @Override
    public void setBeanName(String beanName)
    {
        this.beanName = beanName;
    }
}
