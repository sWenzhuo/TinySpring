package org.example.Service;

import org.example.Spring.ApplicationContext;
import org.example.Spring.ConfigClass;

public class Test {
    public static void main(String[] args) throws ClassNotFoundException {
        ApplicationContext context = new ApplicationContext(ConfigClass.class);

        UserService userservice = (UserService) context.getBean("userservice");
    }
}
