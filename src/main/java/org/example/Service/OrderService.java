package org.example.Service;

import org.example.Spring.AwareBeanName;
import org.example.Spring.Component;


@Component("orderService")
public class OrderService implements AwareBeanName {
    private String beanName;

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }


}
