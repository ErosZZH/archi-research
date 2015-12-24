package com.rick.archi.soa.dubbo.provider;

public class DemoServiceImpl implements DemoService {

	@Override
	public String sayHello(String name) {
		return "Hi dubbo, hi " + name;
	}

}
