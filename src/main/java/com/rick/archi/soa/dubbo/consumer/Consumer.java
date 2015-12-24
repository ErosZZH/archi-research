package com.rick.archi.soa.dubbo.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.rick.archi.soa.dubbo.provider.DemoService;

public class Consumer {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:config/spring/dubbo-consumer.xml");
		context.start();
		DemoService demoService = (DemoService) context.getBean("demoService"); 
		String hello = demoService.sayHello("world"); 
		System.out.println(hello);
		context.stop();
	}
}
