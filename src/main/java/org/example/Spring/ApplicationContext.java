package org.example.Spring;
import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.sql.SQLOutput;
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
                for(File f : files) {

                    //根据反射获取对应的Class对象

                    String absolutePath = f.getAbsolutePath().replace("\\", ".");
                    String className = absolutePath.substring(absolutePath.lastIndexOf("org"), absolutePath.lastIndexOf("."));


                    Class cs = Class.forName(className);

                    //如果有Component注解
                    if (cs.isAnnotationPresent(Component.class)) {

                        Component component = (Component) cs.getAnnotation(Component.class);
                        String beanName = component.value();
                        //判断单例和多例
                        if (cs.isAnnotationPresent(Scoped.class)) {
                            Scoped scoped = (Scoped) cs.getAnnotation(Scoped.class);
                            String scopedType = scoped.value();

                            BeanDefinition beanDefinition = new BeanDefinition(cs, scopedType);
                            beanDefinitionMap.put(beanName, beanDefinition);
                        } else {
                            BeanDefinition beanDefinition = new BeanDefinition(cs, "singleton");
                            beanDefinitionMap.put(beanName, beanDefinition);
                        }
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
                //目录
            }
        }
        //判断注解获取扫描路径
    }
    public Object create(String beanName, BeanDefinition beanDefinition) {
        //根据反射创建bean对象
        try {
            Class objclass = beanDefinition.getType();
            Constructor constructor = objclass.getConstructor();
            Object beanObject = constructor.newInstance();
            //根据beanObject实现依赖注入
            for (Field field : objclass.getDeclaredFields()) {
                if(beanDefinition.getValue().equals("singleton"))
                {
                    //判断是否有autowired注解
                    if(field.isAnnotationPresent(Autowired.class)) {
                        Autowired autowired = field.getAnnotation(Autowired.class);
                        if(beanSigtonParasMap.containsKey(field.getName()))
                        {
                            field.setAccessible(true);
                            field.set(beanObject,beanSigtonParasMap.get(field.getName()));
                        }
                        else{
                            Object bean = create(field.getName(), beanDefinitionMap.get(field.getName()));
                            field.setAccessible(true);
                            field.set(beanObject,bean);
                            beanSigtonParasMap.put(field.getName(),bean);
                        }

                    }
                }
                else {
                    if (field.isAnnotationPresent(Autowired.class)) {
                        field.setAccessible(true);
                        Object bean = create(field.getName(), beanDefinitionMap.get(field.getName()));
                        field.set(beanObject, bean);
                    }

                }

            }
            //实现araw回调给属性赋值,但是需要判断有没有该方法，所以使用接口
            if(beanObject instanceof AwareBeanName)
            {
                //如果实现了AwareBeanName接口,则赋值
                ((AwareBeanName)beanObject).setBeanName(beanName); //其实也可以转化为UserService类型，但是不具有通用型

            }
            if(beanObject instanceof  Aop)
            {
                //实现AOP
                Object proxyInstance = Proxy.newProxyInstance(
                        ApplicationContext.class.getClassLoader(),
                        new Class[]{Aop.class},
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                System.out.println("aop之前");
                                method.invoke(beanObject,args);
                                System.out.println("aop之后");
                                return null;
                            }
                        }
                );
                //实现Aop的代理对象
                return proxyInstance;

            }
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
