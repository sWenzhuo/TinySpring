package org.example.Spring;


import javax.security.auth.login.Configuration;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

//spring 容器
public class ApplicationContext {
    private Class config;

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
                    System.out.println(f.getName());
                    //根据反射获取对应的Class对象
                    Class cs = Class.forName(f.getAbsolutePath());

                    //如果有Component注解
                    if(cs.isAnnotationPresent(Component.class)){
                        //利用反射生成Bean


                    }

                }

                //目录

            }



        }

        //判断注解获取扫描路径

    }

    Map<String,Object>beanSigtonParas = new HashMap<String,Object>();



    public Object getBean(String beanName)
    {


        return null;
    }




}
