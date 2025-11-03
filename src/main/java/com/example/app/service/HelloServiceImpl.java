package com.example.app.service;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "hello " + name;
    }

    @Override
    public String sayGoodbye(String name) {
        return "goodbye " + name;
    }
}
