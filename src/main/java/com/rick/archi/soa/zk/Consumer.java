package com.rick.archi.soa.zk;


import com.rick.archi.soa.service.CalculateService;
import com.rick.archi.soa.service.GreetingService;

public class Consumer {
	
	public static void main(String[] args) throws Exception {
        GreetingService service = RpcFramework.getInstance().subscribe(GreetingService.class);
        CalculateService calService = RpcFramework.getInstance().subscribe(CalculateService.class);

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
