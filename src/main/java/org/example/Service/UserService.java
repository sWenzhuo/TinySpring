package org.example.Service;


import org.example.Spring.Component;
import org.example.Spring.Scoped;

@Component("userService")
@Scoped("prototype")
public class UserService {

    private String name;
    private int age;
}
