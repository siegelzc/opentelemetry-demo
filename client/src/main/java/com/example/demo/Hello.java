package com.example.demo;

import org.springframework.stereotype.Component;

@Component
public class Hello {
    public Hello() {
        hello();
    }

    public void hello() {
        System.out.println("hello");
    }
}
