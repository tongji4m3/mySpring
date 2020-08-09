package com.tongji;

import com.spring.AnnotationConfigApplicationContext;
import com.tongji.service.OrderService;

public class MainTest
{
    public static void main(String[] args)
    {
        //启动Spring
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        //getBean()
        OrderService orderService1=(OrderService)applicationContext.getBean("orderService");

        System.out.println(orderService1);

        orderService1.test();
    }
}
