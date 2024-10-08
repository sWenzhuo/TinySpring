package org.example.CircleService;

import org.example.Spring.CircleContext;
import org.example.Spring.ConfigClass;

public class TestCircle {

    public static void main(String[] args) throws ClassNotFoundException {

        CircleContext circleContext = new CircleContext(ConfigClass.class);
        A a = (A)circleContext.getBean("a");
        a.test();



    }
}
