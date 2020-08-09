package com.tongji.service;

import com.spring.Component;
import com.spring.Scope;

@Component("userService")
@Scope("prototype")
public class UserService
{
    private String username;
    private String password;

    public UserService()
    {
        username = "zhangsan";
        password = "123";
    }

    public UserService(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString()
    {
        return "UserService+"+Integer.toHexString(hashCode())+"{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
