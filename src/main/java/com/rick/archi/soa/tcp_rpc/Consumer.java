package com.rick.archi.soa.tcp_rpc;


import com.rick.archi.soa.service.GreetingService;

public class Consumer {
	
	public static void main(String[] args) throws Exception {
        GreetingService service = RpcFramework.subscribe(GreetingService.class, "127.0.0.1", 3456);
        for (int i = 0; i < Integer.MAX_VALUE; i ++) {
            String value = service.sayHello("Rick" + i);
            String value1 = service.sayGoodBye("Tom" + i);
            System.out.println(value);
            System.out.println(value1);
            Thread.sleep(1000);  
        }  
    }
}
