package com.rick.archi.soa.tcp_rpc;


public class Consumer {
	
	public static void main(String[] args) throws Exception {  
        SayHelloService service = RpcFramework.refer(SayHelloService.class, "127.0.0.1", 3456);  
        for (int i = 0; i < Integer.MAX_VALUE; i ++) {  
            String value = service.sayHello("key_" + i);  
            System.out.println(value);  
            Thread.sleep(1000);  
        }  
    }
}
