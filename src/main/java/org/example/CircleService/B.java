package org.example.CircleService;

import org.example.Spring.Autowired;
import org.example.Spring.Component;


@Component("b")
public class B {
    @Autowired
    private A a;

    @Autowired
    private C c;

    public void test()
    {
        this.c.test();
    }
}
