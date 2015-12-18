package com.rick.archi.soa.tcp_rpc;


public class Provider {
	
	public static void main(String[] args) throws Exception {  
        SayHelloService service = new SayHelloServiceImpl();  
        RpcFramework.publish(service, 3456);  
    } 
}
