package com.rick.archi.soa.tcp_rpc;


import com.rick.archi.soa.service.GreetingService;
import com.rick.archi.soa.service.GreetingServiceImpl;

public class Provider {
	
	public static void main(String[] args) throws Exception {
        GreetingService service = new GreetingServiceImpl();
        RpcFramework.publish(service, 3456);  
    } 
}
