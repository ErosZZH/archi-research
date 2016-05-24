package com.rick.archi.soa.nio_tcp_rpc;


import com.rick.archi.soa.service.CalculateService;
import com.rick.archi.soa.service.GreetingService;

public class Consumer {
	
	public static void main(String[] args) throws Exception {  
        GreetingService service = RpcFramework.subscribe(GreetingService.class, "127.0.0.1", 3456);
        CalculateService calService = RpcFramework.subscribe(CalculateService.class, "127.0.0.1", 6543);
        for (int i = 0; i < Integer.MAX_VALUE; i ++) {  
            String value = service.sayHello("Rick" + i);
            String value1 = service.sayGoodBye("Tom" + i);
            System.out.println(value);
            System.out.println(value1);

            String res = calService.add(1 + i, 1);
            String res1 = calService.minus(1 + i, 1);
            System.out.println(res);
            System.out.println(res1);

            Thread.sleep(1000);  
        }  
    }
}
