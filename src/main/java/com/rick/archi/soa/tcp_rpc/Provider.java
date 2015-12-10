package com.rick.archi.soa.tcp_rpc;


public class Provider {
	
	public static void main(String[] args) throws Exception {  
        SayHelloService service = new SayHelloServiceImpl();  
        RpcFramework.export(service, 3456);  
    } 
}
