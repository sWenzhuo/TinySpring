package org.example.CircleService;

import org.example.Spring.Autowired;
import org.example.Spring.Component;


@Component("a")
public class A {

    @Autowired
    private B b;



    public void test()
    {
        this.b.test();
    }
}
