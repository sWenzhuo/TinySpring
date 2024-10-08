package org.example.CircleService;

import org.example.Spring.Autowired;
import org.example.Spring.Component;


@Component("c")
public class C {

    @Autowired
    private A a;

    @Autowired
    private B b;

    @Autowired
    private D d;


    public void  test()
    {
        this.d.test();
    }
}
