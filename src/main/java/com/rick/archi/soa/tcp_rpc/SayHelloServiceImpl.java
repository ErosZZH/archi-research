package com.rick.archi.soa.tcp_rpc;

public class SayHelloServiceImpl implements SayHelloService {

	@Override
	public String sayHello(String helloArg) {
		return "You said " + helloArg;
	}

}
