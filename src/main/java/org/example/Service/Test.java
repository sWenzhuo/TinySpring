package org.example.Service;

import org.example.Spring.ApplicationContext;
import org.example.Spring.ConfigClass;

public class Test {
    public static void main(String[] args) throws ClassNotFoundException {
        ApplicationContext context = new ApplicationContext(ConfigClass.class);

        UserService userservice = (UserService) context.getBean("userService");

        UserService userservice1 = (UserService) context.getBean("userService");
        UserService userservice2= (UserService) context.getBean("userService");
        UserService userservice3 = (UserService) context.getBean("userService");

        System.out.println(userservice1.hashCode());

        System.out.println(userservice2.hashCode());

        System.out.println(userservice3.hashCode());
    }
}
