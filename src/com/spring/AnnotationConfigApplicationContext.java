package com.spring;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationConfigApplicationContext
{
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public AnnotationConfigApplicationContext(Class configClass)
    {
        //扫描类  ctrl+shift+M快速抽取方法
        List<Class> classList = scan(configClass);

        for (Class aClass : classList)
        {
            //要有Component注解才加载
            if(aClass.isAnnotationPresent(Component.class))
            {
                Component component = (Component) aClass.getAnnotation(Component.class);
                String beanName = component.value();
                //orderService
                //userService
                //System.out.println(beanName);


                //存储扫描到的的类的信息
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setBeanClass(aClass);

                //不然一些没有@Scope的报错
                //还报错则要主要Scope注解上是否写了元注解
                if(aClass.isAnnotationPresent(Scope.class))
                {
                    Scope scope=(Scope)aClass.getAnnotation(Scope.class);
                    beanDefinition.setScope(scope.value());
                }
                else
                {
                    //没写默认为单例
                    beanDefinition.setScope("singleton");
                }

                //遇到一些功能方法暂时没有时间实现的或者是一些问题待解决的，
                // 可以使用TODO标签来标识这些地方，下次通过idea查找TODO标签窗口，
                // 可以快速查询到当前项目工程中需要解决TODO的问题
                //idea右下角有个6.TODO按钮
                //todo 假设都是单例

                //判断aClass是不是前者的子类 放入beanPostProcessorList
                if(BeanPostProcessor.class.isAssignableFrom(aClass))
                {
                    try
                    {
                        BeanPostProcessor beanPostProcessor = (BeanPostProcessor) aClass.getDeclaredConstructor().newInstance();
                        beanPostProcessorList.add(beanPostProcessor);
                    }
                    catch (InstantiationException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                    catch (InvocationTargetException e)
                    {
                        e.printStackTrace();
                    }
                    catch (NoSuchMethodException e)
                    {
                        e.printStackTrace();
                    }
                }

                beanDefinitionMap.put(beanName, beanDefinition);
            }
        }

        //生成单例Bean--放入单例池中保存
        for (String beanName : beanDefinitionMap.keySet())
        {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if(beanDefinition.getScope().equals("singleton"))
            {
                //单例的创建bean,包括他们的自动注入的属性
                Object bean = createBean(beanName,beanDefinition);
                singletonObjects.put(beanName, bean);//放入单例池中
            }
        }
    }

    private Object createBean(String beanName,BeanDefinition beanDefinition)
    {
        //生成该bean
        //实例化
        Class beanClass = beanDefinition.getBeanClass();
        try
        {
            Object bean = beanClass.getDeclaredConstructor().newInstance();

            //填充属性,依赖注入
            //查看是否有字段需要自动注入
            Field[] fields = beanClass.getDeclaredFields();
            for (Field field : fields)
            {
                if (field.isAnnotationPresent(Autowired.class))
                {
                    //所以依赖注入的,必须是Component过的
                    Object object = getBean(field.getName());
                    field.setAccessible(true);
                    field.set(bean,object);
                }
            }
            //Aware
            if(bean instanceof BeanNameAware)
            {
                ((BeanNameAware)bean).setBeanName(beanName);
            }

            //初始化之前可以做的事情 aop
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList)
            {
                beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
            }

            //初始化
            //实现了该接口
            if(bean instanceof InitializingBean)
            {
                ((InitializingBean)bean).afterPropertiesSet();
            }

            //初始化之后可以做的事情
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList)
            {
                beanPostProcessor.postProcessAfterInitialization(bean, beanName);
            }

            //返回bean,而不是beanClass,改了好久的bug
            return bean;
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private List<Class> scan(Class configClass)
    {
        List<Class> classList = new ArrayList<>();

        //如果配置类中不存在ComponentScan这个注解
        if(!configClass.isAnnotationPresent(ComponentScan.class))
        {

        }

        //拿到ComponentScan的内容 即得到扫描的包
        ComponentScan componentScan=(ComponentScan)configClass.getAnnotation(ComponentScan.class);
        String scanPath = componentScan.value();

        //从com.tongji.service转换成com/tongji/service
        scanPath=scanPath.replace(".", "/");

        //扫描类
        ClassLoader classLoader = AnnotationConfigApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(scanPath);

        //System.out.println(resource.getFile());
        //resource.getFile()是String路径
        ///C:/code_home/idea_home/mySpring/out/production/mySpring/com/tongji/service
        File file = new File(resource.getFile());//得到了service目录
        File[] files = file.listFiles();
        //指定目录下的文件,不管是class,还是txt,都扫描
        //一般来说还要递归,现在简化
        //C:\code_home\idea_home\mySpring\out\production\mySpring\com\tongji\service\OrderService.class
        //C:\code_home\idea_home\mySpring\out\production\mySpring\com\tongji\service\UserService.class
        for (File f : files)
        {
            String absolutePath = f.getAbsolutePath();
            absolutePath=absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"));
            absolutePath = absolutePath.replace("\\",".");
            //传入全限定类名才行
            //com.tongji.service.OrderService
            //com.tongji.service.UserService
            try
            {
                Class<?> aClass = classLoader.loadClass(absolutePath);

                classList.add(aClass);
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        return classList;
    }

    public Object getBean(String beanName)
    {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if(beanDefinition.getScope().equals("prototype"))
        {
            return createBean(beanName,beanDefinition);
        }
        else
        {
            Object bean = singletonObjects.get(beanName);
            if(bean==null)//没拿到就创建
            {
                bean = createBean(beanName,beanDefinition);
                singletonObjects.put(beanName, bean);
            }
            return bean;
        }
    }
}
