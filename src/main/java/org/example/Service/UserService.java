package org.example.Service;


import org.example.Spring.*;

@Component("userService")
@Scoped("singleton")
public class UserService implements AwareBeanName,Aop {

    private String name;
    private int age;

    private String beanName;

    @Autowired
    private OrderService orderService;

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void aop() {
        System.out.println(beanName+"执行AOP");
        System.out.println("依赖注入:"+orderService.getBeanName());
    }



}
