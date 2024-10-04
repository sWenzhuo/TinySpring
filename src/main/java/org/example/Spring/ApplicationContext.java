package org.example.Spring;


import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//spring 容器
public class ApplicationContext {
    private Class config;
    private Map<String,Object>beanSigtonParasMap = new HashMap<String,Object>();
    private Map<String,BeanDefinition>beanDefinitionMap = new ConcurrentHashMap<String,BeanDefinition>();
    
    public ApplicationContext(Class config) throws ClassNotFoundException {
        this.config = config;

        if(this.config.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan scan = (ComponentScan) this.config.getAnnotation(ComponentScan.class);
            //org.example.Service
            //获取scan的绝对路径
            String path = scan.value();

            path = path.replace(".","/");// org/example/Service

            //类加载器会通过ApplicationContext.class加载ApplicationContext,所以类加载器知道ApplicationContext.class的地址
            URL resource = ApplicationContext.class.getClassLoader().getResource(path);
            System.out.println(resource);// ile:/D:/myprojects/TinySpring/target/classes/org/example/Service
            File file = new File(resource.getFile());
            if(file.isDirectory())
            {
                File[] files = file.listFiles();
                for(File f : files)
                {

                    //根据反射获取对应的Class对象

                    String absolutePath = f.getAbsolutePath().replace("\\",".");
                    String className = absolutePath.substring(absolutePath.lastIndexOf("org"),absolutePath.lastIndexOf("."));


                    Class cs = Class.forName(className);

                    //如果有Component注解
                    if(cs.isAnnotationPresent(Component.class)){

                        Component component =(Component)cs.getAnnotation(Component.class);
                        String beanName = component.value();
                        //判断单例和多例
                        if(cs.isAnnotationPresent(Scoped.class))
                        {
                            Scoped scoped = (Scoped)cs.getAnnotation(Scoped.class);
                            String scopedType = scoped.value();

                            BeanDefinition beanDefinition = new BeanDefinition(cs, scopedType);

                            beanDefinitionMap.put(beanName, beanDefinition);

                        }
                        else{
                            BeanDefinition beanDefinition = new BeanDefinition(cs, "singleton");
                            beanDefinitionMap.put(beanName,beanDefinition);
                        }
                    }
                    //处理beanDefinition
                    for(String beanName : beanDefinitionMap.keySet())
                    {
                        if(beanDefinitionMap.get(beanName).getValue().equals("singleton"))
                        {
                            if(!beanSigtonParasMap.containsKey(beanName))
                            {
                                Object obj=  this.create(beanName,  beanDefinitionMap.get(beanName));
                                beanSigtonParasMap.put(beanName,obj);
                            }

                        }

                    }
                }

                //目录

            }
        }
        //判断注解获取扫描路径

    }
    public Object create(String beanName, BeanDefinition beanDefinition) {

        //根据反射创建bean对象
        try {
            Constructor constructor = beanDefinition.getType().getConstructor();
            Object beanObject = constructor.newInstance();
            return beanObject;
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new RuntimeException("没有找到 Bean: " + beanName);
        }
        if ("singleton".equals(beanDefinition.getValue())) {
            Object beanObject = beanSigtonParasMap.get(beanName);
            if (beanObject == null) {
                throw new RuntimeException("没有找到单例Bean: " + beanName);
            }
            return beanObject;
        } else if ("prototype".equals(beanDefinition.getValue())) {
            return create(beanName,beanDefinition);
        } else {
            throw new RuntimeException("未知的 Bean 作用域: " + beanDefinition.getValue());
        }
    }




}
