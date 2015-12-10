package com.rick.archi.soa.tcp_rpc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolHelper {

	private static ExecutorService executor;
	
	static {
		executor = Executors.newFixedThreadPool(20);
	}
	
	public static ExecutorService getExecutorInstance() {
		return executor;
	}
}
