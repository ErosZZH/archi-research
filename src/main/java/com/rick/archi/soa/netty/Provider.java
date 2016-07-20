package com.rick.archi.soa.netty;


import com.rick.archi.soa.service.CalculateService;
import com.rick.archi.soa.service.CalculateServiceImpl;
import com.rick.archi.soa.service.GreetingService;
import com.rick.archi.soa.service.GreetingServiceImpl;

import java.util.HashMap;
import java.util.Map;

public class Provider {
	
	public static void main(String[] args) throws Exception {  
        GreetingService service = new GreetingServiceImpl();
        CalculateService calService = new CalculateServiceImpl();
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(3456, service);
        map.put(6543, calService);
        RpcFramework.publish(map);
    } 
}
