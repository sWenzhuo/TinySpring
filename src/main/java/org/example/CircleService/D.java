package org.example.CircleService;

import org.example.Spring.Autowired;
import org.example.Spring.Component;


@Component("d")
public class D {

    @Autowired
    private  A a;


    @Autowired
    private B b;


    @Autowired
    private C c;



    public void test()
    {
        System.out.println("输出d");
    }
}
