package org.example.Service;


import org.example.Spring.Aop;
import org.example.Spring.AwareBeanName;
import org.example.Spring.Component;
import org.example.Spring.Scoped;

@Component("userService")
@Scoped("singleton")
public class UserService implements AwareBeanName,Aop {

    private String name;
    private int age;

    private String beanName;

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }


    @Override
    public void aop() {
        System.out.println(beanName+"执行AOP");
    }
}
