package org.example.Spring;


import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//模拟循环依赖
public class CircleContext {


    private Class config;

    private Map<String,BeanDefinition> beanDefinitionMap = new HashMap<String,BeanDefinition>();
    private Map<String, Object>singletonObjects = new HashMap<String,Object>();

    public CircleContext(Class config) throws ClassNotFoundException {
        this.config = config;

        if(this.config.isAnnotationPresent(ComponentScan.class))
        {
            ComponentScan annotation = (ComponentScan) this.config.getAnnotation(ComponentScan.class);
            String path = annotation.value();

            path = path.replace(".","/");
            URL absoultPath = CircleContext.class.getClassLoader().getResource(path);

            //找到该包下的所有.class文件
            //类加载器会通过ApplicationContext.class加载ApplicationContext,所以类加载器知道ApplicationContext.class的地址

            System.out.println(absoultPath);// ile:/D:/myprojects/TinySpring/target/classes/org/example/Service
            File file = new File(absoultPath.getFile());
            if(file.isDirectory())
            {
                File[] files = file.listFiles();
                for(File f : files)
                {
                    String absolutePath = f.getAbsolutePath().replace("\\", ".");
                    String className = absolutePath.substring(absolutePath.lastIndexOf("org"), absolutePath.lastIndexOf("."));

                    Class cs = Class.forName(className);
                    if (cs.isAnnotationPresent(Component.class)) {
                        Component component =(Component) cs.getAnnotation(Component.class);
                        String beanName = component.value();
                        BeanDefinition beanDefinition = new BeanDefinition(cs, "singleton"); //默认是单例bean
                        beanDefinitionMap.put(beanName, beanDefinition);

                    }

                }
                //根据beanDefinitionMap创建bean对象
                for(String beanName : beanDefinitionMap.keySet())
                {
                    Object bean = createBean(beanName, beanDefinitionMap.get(beanName));
                    singletonObjects.put(beanName, bean);

                }

            }

        }
    }

    public Object createBean(String beanName,BeanDefinition beanDefinition)
    {
        try {
            Class cs = beanDefinition.getType();
            Constructor constructor=cs.getConstructor();
            Object bean = constructor.newInstance();
            //依赖注入,创建Aservice对象要依赖注入Bservice对象,但是目前单例池没有Bservice对象,创建Bservice对象的时候也需要Aservice对象，所以也不行。
            //第一种方式Aservice让步给Bservice,先不注入,直接加入到单例池,等Bservice注入后再注入Aservice,实现一级缓存,但是由于是递归调用createBean,所以也不知道什么时候能够注入，什么时候不能够注入
            //考虑到最复杂的一种情况A依赖B    B依赖A,C   C依赖A,B,D  D依赖A,B,C   我们发现首先需要注入的是D中的A,B,C然后是C中的A,B,D然后B中的A,C最后再注入A，也就是说先注入最底层。
            //递归调用的时候先把所有的对象放入单例池
            singletonObjects.put(beanName, bean);

            for (Field field : cs.getDeclaredFields()) {
                if(field.isAnnotationPresent(Autowired.class))
                {
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    if(!singletonObjects.containsKey(field.getName())) {
                        //递归创建
                        createBean(field.getName(), beanDefinitionMap.get(field.getName()));
                    }

                }
            }
            //全部存在则直接注入
            for (Field field : cs.getDeclaredFields()) {
                if(field.isAnnotationPresent(Autowired.class))
                {
                    Object beanObj = singletonObjects.get(field.getName());
                    field.setAccessible(true);
                    field.set(bean, beanObj);
                }
            }
            return bean;




        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
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
            Object beanObject = singletonObjects.get(beanName);
            if (beanObject == null) {
                throw new RuntimeException("没有找到单例Bean: " + beanName);
            }
            return beanObject;
        } else if ("prototype".equals(beanDefinition.getValue())) {
            return createBean(beanName,beanDefinition);
        } else {
            throw new RuntimeException("未知的 Bean 作用域: " + beanDefinition.getValue());
        }
    }

}
