package org.example.Spring;

public class BeanDefinition {

    private Class type;
    private String value;


    public BeanDefinition(Class type, String value) {
        this.type = type;
        this.value = value;
    }


    public Class getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
