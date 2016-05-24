package com.rick.archi.soa.service;

/**
 * Created by rick on 16/5/23.
 */
public class CalculateServiceImpl implements CalculateService {

    @Override
    public String add(Integer a, Integer b) {
        return a + "+" + b + "=" + (a + b);
    }

    @Override
    public String minus(Integer a, Integer b) {
        return a + "-" + b + "=" + (a - b);
    }
}
