package com.rick.archi.soa.service;

/**
 * Created by rick on 16/5/23.
 */
public class GreetingServiceImpl implements GreetingService {

    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }

    @Override
    public String sayGoodBye(String name) {
        return "Bye " + name;
    }
}
